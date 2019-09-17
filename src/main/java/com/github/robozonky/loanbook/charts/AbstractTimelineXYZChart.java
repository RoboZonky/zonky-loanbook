package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
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
import io.vavr.Tuple2;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public abstract class AbstractTimelineXYZChart extends AbstractXYZChart {

    private static final BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    protected AbstractTimelineXYZChart(final Data data, final XYZChartDataProcessor processor) {
        super(data, processor);
    }

    protected static void abstractOriginationTimeline(final Stream<DataRow> data,
            final XYZChartDataConsumer adder, final Tuple2<String, Function<List<DataRow>, Number>>... metrics) {
        abstractTimeline(data, adder, d -> d.collect(
                collectingAndThen(
                        groupingBy(DataRow::getOrigin,
                                toList()),
                        TreeMap::new
                )), metrics);
    }

    protected static void abstractTimeline(final Stream<DataRow> data, final XYZChartDataConsumer adder,
            final Function<Stream<DataRow>, SortedMap<YearMonth, List<DataRow>>> mapper,
            final Tuple2<String, Function<List<DataRow>, Number>>... metrics) {
        final SortedMap<YearMonth, List<DataRow>> all = mapper.apply(data);
        all.forEach((yearMonth, rows) -> {
            for (final Tuple2<String, Function<List<DataRow>, Number>> metric : metrics) {
                final Number raw = metric._2.apply(rows);
                final Number value = raw instanceof Ratio ? raw.doubleValue() * 100 : raw;
                adder.accept(Tuple.of(yearMonth.toString(), metric._1, value));
            }
        });
    }

    private static SortedMap<YearMonth, List<DataRow>> allActiveUntilGivenDate(final Stream<DataRow> data) {
        final SortedMap<YearMonth, List<DataRow>> result = new TreeMap<>();
        data.forEach(row -> {
            final YearMonth started = row.getOrigin();
            final YearMonth finished = row.getFinished().orElse(row.getReportDate().minusMonths(1));
            YearMonth current = started;
            do {
                final List<DataRow> matching = result.computeIfAbsent(current, key -> new ArrayList<>());
                matching.add(row);
                current = current.plusMonths(1);
            } while (!current.isAfter(finished));
        });
        return result;
    }

    protected static void abstractGlobalTimeline(final Stream<DataRow> data,
            final XYZChartDataConsumer adder,
            final Tuple2<String, Function<List<DataRow>, Number>>... metrics) {
        abstractTimeline(data, adder, AbstractTimelineXYZChart::allActiveUntilGivenDate, metrics);
    }

    protected static void abstractInterestRateHealthTimeline(final Stream<DataRow> data,
            final Predicate<DataRow> howHealthy,
            final XYZChartDataConsumer adder) {
        final TreeMap<YearMonth, TreeMap<Ratio, List<DataRow>>> byMonthAndRating =
                data.collect(
                        collectingAndThen(
                                groupingBy(r -> YearMonth.from(r.getOrigin()),
                                        collectingAndThen(
                                                groupingBy(DataRow::getInterestRate,
                                                        toList()),
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
                .collect(toCollection(TreeSet::new));
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

    @Override
    public StackingType getStacking() {
        return StackingType.NONE;
    }

    @Override
    public String getLabelForX() {
        return "Datum originace";
    }

    @Override
    public ChartType getType() {
        return ChartType.LINE;
    }
}
