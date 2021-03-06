package com.github.robozonky.loanbook.charts;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public final class SummaryInterestRateCountTimelineChart extends AbstractTimelineXYZChart {

    public SummaryInterestRateCountTimelineChart(final Data data) {
        super(data, SummaryInterestRateCountTimelineChart::interestRateDefaultTimeline);
    }

    private static Number convert(final Ratio ratio, final List<DataRow> rows) {
        return rows.stream()
                .filter(r -> r.getInterestRate().equals(ratio))
                .count();
    }

    private static Tuple2<String, Function<List<DataRow>, Number>> convert(final Ratio ratio) {
        return Tuple.of(ratio + " p.a.", data ->  convert(ratio, data));
    }

    private static void interestRateDefaultTimeline(final Stream<DataRow> data,
                                                    final XYZChartDataConsumer adder) {
        final SortedMap<Ratio, List<DataRow>> all =
                data.collect(
                        collectingAndThen(
                                groupingBy(DataRow::getInterestRate,
                                           toList()),
                                TreeMap::new
                        ));
        final Tuple2<String, Function<List<DataRow>, Number>>[] series = all.keySet().stream()
                .map(SummaryInterestRateCountTimelineChart::convert)
                .toArray((IntFunction<Tuple2<String, Function<List<DataRow>, Number>>[]>) Tuple2[]::new);
        abstractOriginationTimeline(all.values().stream().flatMap(Collection::stream), adder, series);
    }

    @Override
    public StackingType getStacking() {
        return StackingType.ABSOLUTE;
    }

    @Override
    public ChartType getType() {
        return ChartType.COLUMN;
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
        return "Počet [ks]";
    }

    @Override
    public String getTitle() {
        return "Počet půjček v čase";
    }
}
