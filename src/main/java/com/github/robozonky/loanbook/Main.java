package com.github.robozonky.loanbook;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    private static void abstractRiskChartCustomSorted(final Data data,
                                                      final Function<DataRow, CustomSortString> parameter,
                                                      final Consumer<Tuple3<String, String, Number>> adder) {
        final TreeMap<Ratio, TreeMap<CustomSortString, List<DataRow>>> byInterestRateAndSecond =
                data.getAll().collect(
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(DataRow::getInterestRate,
                                                      Collectors.collectingAndThen(
                                                              Collectors.groupingBy(parameter, Collectors.toList()),
                                                              TreeMap::new)),
                                TreeMap::new
                        ));
        // count totals
        final Map<Ratio, Map<CustomSortString, LongAdder>> totals = new HashMap<>(0);
        final Map<Ratio, Map<CustomSortString, LongAdder>> defaultedTotals = new HashMap<>(0);
        byInterestRateAndSecond.forEach((ratio, sub) -> sub.forEach((second, rows) -> {
            totals.computeIfAbsent(ratio, __ -> new HashMap<>())
                    .computeIfAbsent(second, __ -> new LongAdder())
                    .add(rows.size());
            final long defaulted = rows.stream().filter(DataRow::isDefaulted).count();
            defaultedTotals.computeIfAbsent(ratio, __ -> new HashMap<>())
                    .computeIfAbsent(second, __ -> new LongAdder())
                    .add(defaulted);
        }));
        // sum total defaults per the second parameter
        final Map<CustomSortString, LongAdder> defaultedPerSecondParameter = new HashMap<>(0);
        defaultedTotals.forEach((__, sub) -> sub.forEach((second, result) -> {
            defaultedPerSecondParameter.computeIfAbsent(second, ___ -> new LongAdder()).add(result.longValue());
        }));
        final Map<CustomSortString, LongAdder> totalPerSecondParameter = new HashMap<>(0);
        totals.forEach((__, sub) -> sub.forEach((second, result) -> {
            totalPerSecondParameter.computeIfAbsent(second, ___ -> new LongAdder()).add(result.longValue());
        }));
        // figure out every possible category, in expected order
        final SortedSet<CustomSortString> everySecond = totals.entrySet().stream()
                .flatMap(entry -> entry.getValue().keySet().stream())
                .collect(Collectors.toCollection(TreeSet::new));
        // figure out the ratio and store into the chart
        byInterestRateAndSecond.forEach((ratio, sub) -> everySecond.forEach(second -> {
            final long totalCount = totalPerSecondParameter.getOrDefault(second, new LongAdder()).longValue();
            final String id = second + " (" + defaultedPerSecondParameter.get(
                    second) + " z " + totalPerSecondParameter.get(second) + ")";
            if (totalCount == 0) {
                adder.accept(Tuple.of(id, ratio + " p.a.", BigDecimal.ZERO));
            } else {
                final long defaultedCount = defaultedTotals.getOrDefault(ratio, Collections.emptyMap())
                        .getOrDefault(second, new LongAdder())
                        .longValue();
                final BigDecimal result = defaultedCount == 0 ?
                        BigDecimal.ZERO :
                        BigDecimal.valueOf(defaultedCount)
                                .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_EVEN)
                                .multiply(HUNDRED);
                adder.accept(Tuple.of(id, ratio + " p.a.", result));
            }
        }));
    }

    private static void abstractRiskChart(final Data data, final Function<DataRow, String> parameter,
                                          final Consumer<Tuple3<String, String, Number>> adder) {
        final Function<DataRow, CustomSortString> convertor = r -> new CustomSortString(parameter.apply(r));
        abstractRiskChartCustomSorted(data, convertor, adder);
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

    private static void principalRiskChart(final Data data, final Consumer<Tuple3<String, String, Number>> adder) {
        abstractRiskChartCustomSorted(data, r -> {
            final int step = 50_000;
            final int cycle = (r.getAmount().intValue() - 1) / step;
            final int start = (cycle * step) / 1000;
            final int end = ((cycle + 1) * step) / 1000;
            if (cycle == 0) {
                return new CustomSortString(" do " + end, cycle);
            } else if (cycle > 13) {
                return new CustomSortString(" od " + start, cycle);
            } else {
                return new CustomSortString("od " + start + " do " + end, cycle);
            }
        }, adder);
    }

    private static void termRiskChart(final Data data, final Consumer<Tuple3<String, String, Number>> adder) {
        abstractRiskChartCustomSorted(data, r -> {
            final int step = 12;
            final int cycle = (r.getOriginalInstalmentCount() - 1) / step;
            final int start = (cycle * step);
            final int end = ((cycle + 1) * step);
            if (cycle == 0) {
                return new CustomSortString(" do " + end, cycle);
            } else if (cycle > 5) {
                return new CustomSortString(" od " + start, cycle);
            } else {
                return new CustomSortString("od " + start + " do " + end, cycle);
            }
        }, adder);
    }

    private static void saveJs(final String filename) {
        try (final InputStream s = Main.class.getResourceAsStream(filename)) {
            final byte[] bytes = s.readAllBytes();
            Files.write(Path.of(filename), bytes);
            LOGGER.info("Saved {}.", filename);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void interestRateDefaultTimeline(final Data data,
                                                    final Consumer<Tuple3<String, String, Number>> adder) {
        abstractInterestRateHealthTimeline(data, DataRow::isDefaulted, adder);
    }

    private static void abstractInterestRateHealthTimeline(final Data data, final Predicate<DataRow> howHealthy,
                                                           final Consumer<Tuple3<String, String, Number>> adder) {
        final TreeMap<YearMonth, TreeMap<Ratio, List<DataRow>>> byMonthAndRating =
                data.getAll().collect(
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(r -> YearMonth.from(r.getOrigin()),
                                                      Collectors.collectingAndThen(
                                                              Collectors.groupingBy(DataRow::getInterestRate,
                                                                                    Collectors.toList()),
                                                              TreeMap::new)),
                                TreeMap::new
                        ));
        // count totals
        final Map<YearMonth, Map<Ratio, LongAdder>> totals = new HashMap<>(0);
        final Map<YearMonth, Map<Ratio, LongAdder>> healthyTotals = new HashMap<>(0);
        byMonthAndRating.forEach((ratio, sub) -> sub.forEach((second, rows) -> {
            totals.computeIfAbsent(ratio, __ -> new HashMap<>())
                    .computeIfAbsent(second, __ -> new LongAdder())
                    .add(rows.size());
            final long healthy = rows.stream().filter(howHealthy).count();
            healthyTotals.computeIfAbsent(ratio, __ -> new HashMap<>())
                    .computeIfAbsent(second, __ -> new LongAdder())
                    .add(healthy);
        }));
        // sum total defaults per the second parameter
        final Map<Ratio, LongAdder> healthyPerSecondParameter = new HashMap<>(0);
        healthyTotals.forEach((__, sub) -> sub.forEach((second, result) -> {
            healthyPerSecondParameter.computeIfAbsent(second, ___ -> new LongAdder()).add(result.longValue());
        }));
        final Map<Ratio, LongAdder> totalPerSecondParameter = new HashMap<>(0);
        totals.forEach((__, sub) -> sub.forEach((second, result) -> {
            totalPerSecondParameter.computeIfAbsent(second, ___ -> new LongAdder()).add(result.longValue());
        }));
        // figure out every possible category, in expected order
        final SortedSet<Ratio> everySecond = totals.entrySet().stream()
                .flatMap(entry -> entry.getValue().keySet().stream())
                .collect(Collectors.toCollection(TreeSet::new));
        // figure out the ratio and store into the chart
        byMonthAndRating.forEach((yearMonth, sub) -> everySecond.forEach(second -> {
            final long totalCount = sub.getOrDefault(second, Collections.emptyList()).size();
            if (totalCount == 0) {
                adder.accept(Tuple.of(yearMonth.toString(), second + " p.a.", BigDecimal.ZERO));
            } else {
                final long healthyCount = healthyTotals.getOrDefault(yearMonth, Collections.emptyMap())
                        .getOrDefault(second, new LongAdder())
                        .longValue();
                final BigDecimal result = healthyCount == 0 ?
                        BigDecimal.ZERO :
                        BigDecimal.valueOf(healthyCount)
                                .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_EVEN)
                                .multiply(HUNDRED);
                adder.accept(Tuple.of(yearMonth.toString(), second + " p.a.", result));
            }
        }));
    }

    private static void interestRateHealthTimeline(final Data data,
                                                   final Consumer<Tuple3<String, String, Number>> adder) {
        abstractInterestRateHealthTimeline(data, r -> r.getMaxDaysPastDue() < 1, adder);
    }

    private static void interestRate30DayTimeline(final Data data,
                                                   final Consumer<Tuple3<String, String, Number>> adder) {
        abstractInterestRateHealthTimeline(data, r -> r.getMaxDaysPastDue() > 0 && r.getMaxDaysPastDue() < 31, adder);
    }

    public static void main(final String... args) {
        final XLSXDownloader downloader = new XLSXDownloader();
        final InputStream s = downloader.get().orElseThrow(() -> new IllegalStateException("No loanbook available."));
        final XLSXConverter c = new XLSXConverter();
        final String[][] result = c.apply(s);
        final Template template = new Template(Data.process(result));
        template.addBarChart("Zesplatněné půjčky podle účelu", "Účel", "Úroková míra [% p.a.]",
                             "Zesplatněno z celku [%]", Main::purposeRiskChart);
        template.addBarChart("Zesplatněné půjčky podle kraje", "Kraj", "Úroková míra [% p.a.]",
                             "Zesplatněno z celku [%]", Main::regionRiskChart);
        template.addBarChart("Zesplatněné půjčky podle zdroje příjmu žadatele", "Zdroj příjmu", "Úroková míra [% p.a.]",
                             "Zesplatněno z celku [%]", Main::incomeRiskChart);
        template.addBarChart("Zesplatněné půjčky podle výše úvěru", "Výše úvěru [tis. Kč]", "Úroková míra [% p.a.]",
                             "Zesplatněno z celku [%]", Main::principalRiskChart);
        template.addBarChart("Zesplatněné půjčky podle délky splácení", "Délka úvěru [měs.]", "Úroková míra [% p.a.]",
                             "Zesplatněno z celku [%]", Main::termRiskChart);
        template.addLineChart("Zesplatnění podle data originace a ratingu [%]", "Datum originace",
                              "Úroková míra [% p.a.]", "Zesplatněno z originovaných [%]",
                              Main::interestRateDefaultTimeline);
        // these don't seem tobe right, fix them first
        /*
        template.addLineChart("Půjčky bez prodlení podle data originace a ratingu [%]", "Datum originace",
                              "Úroková míra [% p.a.]", "Nikdy v prodlení z originovaných [%]",
                              Main::interestRateHealthTimeline);
        template.addLineChart("Půjčky s max. 30 dny prodlení podle data originace a ratingu [%]", "Datum originace",
                              "Úroková míra [% p.a.]", "Nejvýše 30 dní v prodlení z originovaných [%]",
                              Main::interestRate30DayTimeline);

         */
        template.run();
        saveJs("canvg.js");
        saveJs("rgbcolor.js");
        saveJs("svgprint.js");
    }
}
