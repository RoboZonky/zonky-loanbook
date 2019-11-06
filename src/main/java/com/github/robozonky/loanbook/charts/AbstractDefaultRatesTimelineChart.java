package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import io.vavr.Tuple;
import io.vavr.Tuple3;

import static java.time.temporal.ChronoUnit.MONTHS;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

abstract class AbstractDefaultRatesTimelineChart extends AbstractTimelineXYZChart {

    protected AbstractDefaultRatesTimelineChart(final Data data,
                                                final Function<DataRow, CustomSortString> secondParameter) {
        super(data, (d, a) -> interestRateDefaultTimeline(d, secondParameter, a));
    }

    private static long getMaxAge(final DataRow r) {
        return r.getLastDelinquent()
                .map(delinquent -> MONTHS.between(r.getOrigin(), delinquent))
                .orElseGet(() -> r.getFinished()
                        .map(finished -> MONTHS.between(r.getOrigin(), finished))
                        .orElseGet(() -> MONTHS.between(r.getOrigin(), r.getReportDate())));
    }

    private static void interestRateDefaultTimeline(final Stream<DataRow> data,
                                                    final Function<DataRow, CustomSortString> secondParam,
                                                    final XYZChartDataConsumer adder) {
        /*
         * find all loans that lived exactly for a given amount of time; "to live" means to either finish in that time,
         * default in that time, or be active for that time.
         */
        final TreeMap<Long, TreeMap<CustomSortString, List<DataRow>>> byAgeAndSecond = data.collect(
                collectingAndThen(
                        groupingBy(AbstractDefaultRatesTimelineChart::getMaxAge,
                                   collectingAndThen(
                                           groupingBy(secondParam,
                                                      toList()),
                                           TreeMap::new)),
                        TreeMap::new
                ));
        final SortedSet<CustomSortString> allSeconds = byAgeAndSecond.values().stream()
                .flatMap(e -> e.keySet().stream())
                .collect(Collectors.toCollection(TreeSet::new));
        // find all loans that lived for at least a given amount of time
        final TreeMap<Long, TreeMap<CustomSortString, List<DataRow>>> ageAtLeast = new TreeMap<>();
        for (final long leastAge : byAgeAndSecond.keySet()) {
            for (final CustomSortString seconds : allSeconds) {
                final List<DataRow> semi = byAgeAndSecond.tailMap(leastAge).values().stream()
                        .map(e -> e.getOrDefault(seconds, Collections.emptyList()))
                        .flatMap(Collection::stream)
                        .collect(toList());
                ageAtLeast.computeIfAbsent(leastAge, __ -> new TreeMap<>()).put(seconds, semi);
            }
        }
        // count totals
        final Map<Long, Map<CustomSortString, Long>> totals = new HashMap<>();
        final Map<Long, Map<CustomSortString, Long>> defaults = new HashMap<>();
        ageAtLeast.forEach((leastAge, sub) -> sub.forEach((second, rows) -> {
            totals.computeIfAbsent(leastAge, __ -> new HashMap<>())
                    .put(second, (long) rows.size());
            defaults.computeIfAbsent(leastAge, __ -> new HashMap<>())
                    .put(second, rows.stream().filter(DataRow::isDefaulted).count());
        }));
        // figure out the ratio and store into the chart
        ageAtLeast.forEach((leastAge, sub) -> allSeconds.forEach(second -> {
            final long loanCount = totals.getOrDefault(leastAge, Collections.emptyMap())
                    .getOrDefault(second, 0l);
            final long defaultCount = defaults.getOrDefault(leastAge, Collections.emptyMap())
                    .getOrDefault(second, 0l);
            if (loanCount > 0) {
                final BigDecimal result = BigDecimal.valueOf(defaultCount)
                        .divide(BigDecimal.valueOf(loanCount), 4, RoundingMode.HALF_EVEN)
                        .multiply(BigDecimal.TEN)
                        .multiply(BigDecimal.TEN);
                final Tuple3<String, Comparable, Number> t = Tuple.of("" + leastAge, second, result);
                adder.accept(t);
            }
        }));
    }

    @Override
    public String getLabelForX() {
        return "Počet měsíců života půjčky";
    }

    @Override
    public String getLabelForZ() {
        return "Zesplatněno z doživších se [%]";
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of("Za doživší se půjčky nepočítáme půjčky doplacené nebo zesplatněné v měsících předchozích.");
    }
}
