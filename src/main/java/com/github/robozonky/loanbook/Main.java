package com.github.robozonky.loanbook;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import io.vavr.Tuple;
import io.vavr.Tuple3;

public class Main {

    private static void abstractRiskChart(final Data data, final Function<DataRow, String> parameter,
                                          final Consumer<Tuple3<String, String, Number>> adder) {
        final TreeMap<Ratio, TreeMap<String, List<DataRow>>> byInterestRateAndSecond = data.getAll().collect(
                Collectors.collectingAndThen(
                        Collectors.groupingBy(DataRow::getInterestRate,
                                              Collectors.collectingAndThen(
                                                      Collectors.groupingBy(parameter::apply, Collectors.toList()),
                                                      TreeMap::new)),
                        TreeMap::new
                ));
        // count totals
        final Map<Ratio, Map<String, LongAdder>> totals = new HashMap<>(0);
        final Map<Ratio, Map<String, LongAdder>> defaultedTotals = new HashMap<>(0);
        byInterestRateAndSecond.forEach((ratio, sub) -> sub.forEach((second, rows) -> {
            totals.computeIfAbsent(ratio, __ -> new HashMap<>())
                    .computeIfAbsent(second, __ -> new LongAdder())
                    .add(rows.size());
            totals.getOrDefault(ratio, new HashMap<>()).getOrDefault(second, new LongAdder()).add(rows.size());
            final long defaulted = rows.stream().filter(DataRow::isDefaulted).count();
            defaultedTotals.computeIfAbsent(ratio, __ -> new HashMap<>())
                    .computeIfAbsent(second, __ -> new LongAdder())
                    .add(defaulted);
        }));
        // figure out the ratio and store into the chart
        byInterestRateAndSecond.forEach((ratio, sub) -> sub.forEach((second, __) -> {
            final long totalCount = totals.get(ratio).get(second).longValue();
            // some ratings will have no defaults, we need to be careful
            final long defaultedCount = defaultedTotals.getOrDefault(ratio, Collections.emptyMap())
                    .getOrDefault(second, new LongAdder())
                    .longValue();
            final BigDecimal result = BigDecimal.valueOf(defaultedCount)
                    .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_EVEN)
                    .multiply(BigDecimal.TEN)
                    .multiply(BigDecimal.TEN);
            adder.accept(Tuple.of(second, ratio + " p.a.", result));
        }));
    }

    private static void regionRiskChart(final Data data, final Consumer<Tuple3<String, String, Number>> adder) {
        abstractRiskChart(data, DataRow::getRegion, adder);
    }

    private static void purposeRiskChart(final Data data, final Consumer<Tuple3<String, String, Number>> adder) {
        abstractRiskChart(data, DataRow::getPurpose, adder);
    }

    private static void incomeRiskChart(final Data data, final Consumer<Tuple3<String, String, Number>> adder) {
        abstractRiskChart(data, DataRow::getIncomeType, adder);
    }

    public static void main(final String... args) {
        final XLSXDownloader downloader = new XLSXDownloader();
        final InputStream s = downloader.get().orElseThrow(() -> new IllegalStateException("No loanbook available."));
        final XLSXConverter c = new XLSXConverter();
        final String[][] result = c.apply(s);
        final Template template = new Template(Data.process(result));
        template.addBarChart("Zesplatněné půjčky podle kraje", "Kraj", "Úroková míra [% p.a.]",
                                "Zesplatněno z počtu [%]", Main::regionRiskChart);
        template.addBarChart("Zesplatněné půjčky podle zdroje příjmu žadatele", "Zdroj příjmu", "Úroková míra [% p.a.]",
                             "Zesplatněno z počtu [%]", Main::incomeRiskChart);
        template.addBarChart("Zesplatněné půjčky podle účelu", "Účel", "Úroková míra [% p.a.]",
                             "Zesplatněno z počtu [%]", Main::purposeRiskChart);
        template.run();
    }
}
