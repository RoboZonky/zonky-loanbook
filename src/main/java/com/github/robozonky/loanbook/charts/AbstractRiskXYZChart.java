package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import io.vavr.Tuple;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public abstract class AbstractRiskXYZChart extends AbstractXYZChart {

    private static final BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    protected AbstractRiskXYZChart(final Data data, final XYZChartDataProcessor processor) {
        super(data, processor);
    }

    protected static void abstractInterestRateHealthBinary(final Stream<DataRow> data,
                                                           final Predicate<DataRow> firstFilter,
                                                           final Predicate<DataRow> sorter,
                                                           final XYZChartDataConsumer adder) {
        final List<DataRow> allData = data.collect(toList());
        final TreeMap<Ratio, TreeMap<Boolean, List<DataRow>>> byInterestRateAndSecondTotal =
                allData.stream().collect(
                        collectingAndThen(
                                groupingBy(DataRow::getInterestRate,
                                           collectingAndThen(
                                                   groupingBy(sorter::test,
                                                              toList()),
                                                   TreeMap::new)),
                                TreeMap::new
                        ));
        final TreeMap<Ratio, TreeMap<Boolean, List<DataRow>>> byInterestRateAndSecondFiltered =
                allData.stream()
                        .filter(firstFilter)
                        .collect(
                                collectingAndThen(
                                        groupingBy(DataRow::getInterestRate,
                                                   collectingAndThen(
                                                           groupingBy(sorter::test,
                                                                      toList()),
                                                           TreeMap::new)),
                                        TreeMap::new
                                ));
        // figure out every possible category, in expected order
        final SortedSet<Ratio> everySecond = new TreeSet<>(byInterestRateAndSecondTotal.keySet());
        everySecond.forEach(interestRate -> {
            final String id = interestRate + " p.a.";
            for (int i = 0; i <= 1; i++) {
                final boolean matches = i == 1;
                final int count = byInterestRateAndSecondFiltered.getOrDefault(interestRate, new TreeMap<>())
                        .getOrDefault(matches, Collections.emptyList())
                        .size();
                final long totalCount = byInterestRateAndSecondTotal.values().stream()
                        .mapToLong(map -> map.getOrDefault(matches, Collections.emptyList()).size())
                        .sum();
                final long totalFilteredCount = byInterestRateAndSecondFiltered.values().stream()
                        .mapToLong(map -> map.getOrDefault(matches, Collections.emptyList()).size())
                        .sum();
                final String partValue = matches ? "Ano" : "Ne";
                final String value = partValue + " (" + totalFilteredCount + " z " + totalCount + ")";
                final BigDecimal result = count == 0 ?
                        BigDecimal.ZERO :
                        BigDecimal.valueOf(count)
                                .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_EVEN)
                                .multiply(HUNDRED);
                adder.accept(Tuple.of(value, id, result));
            }
        });
    }

    protected static void abstractRiskChart(final Stream<DataRow> data, final Function<DataRow, String> parameter,
                                            final XYZChartDataConsumer adder) {
        final Function<DataRow, CustomSortString> convertor = r -> new CustomSortString(parameter.apply(r));
        abstractRiskChartCustomSorted(data, convertor, adder);
    }

    protected static void abstractRiskChartCustomSorted(final Stream<DataRow> data,
                                                        final Function<DataRow, CustomSortString> parameter,
                                                        final XYZChartDataConsumer adder) {
        final TreeMap<Ratio, TreeMap<CustomSortString, List<DataRow>>> byInterestRateAndSecond =
                data.collect(
                        collectingAndThen(
                                groupingBy(DataRow::getInterestRate,
                                           collectingAndThen(
                                                   groupingBy(parameter, toList()),
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
                .collect(toCollection(TreeSet::new));
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

    @Override
    public boolean isRatingsAsSeries() {
        return true;
    }

    @Override
    public String getLabelForY() {
        return "Úroková míra [% p.a.]";
    }

    @Override
    public String getLabelForZ() {
        return "Zesplatněno z celku [%]";
    }

    @Override
    public ChartType getType() {
        return ChartType.BAR;
    }
}
