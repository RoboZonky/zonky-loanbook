package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public final class SummaryPrincipalTimelineChart extends AbstractTimelineXYZChart {

    public SummaryPrincipalTimelineChart(final Data data) {
        super(data, SummaryPrincipalTimelineChart::regionTimeline);
    }

    private static CustomSortString categorize(final DataRow row) {
        return PrincipalRiskChart.getCategory(row);
    }

    private static Number convert(final CustomSortString category, final List<DataRow> rows) {
        final long total = rows.size();
        final long part = rows.stream().filter(r -> categorize(r).equals(category)).count();
        return BigDecimal.valueOf(part)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.TEN)
                .multiply(BigDecimal.TEN);
    }

    private static Tuple2<CustomSortString, Function<List<DataRow>, Number>> convert(final CustomSortString category) {
        return Tuple.of(category, data ->  convert(category, data));
    }

    private static void regionTimeline(final Stream<DataRow> data, final XYZChartDataConsumer adder) {
        final SortedMap<CustomSortString, List<DataRow>> all =
                data.collect(
                        collectingAndThen(
                                groupingBy(SummaryPrincipalTimelineChart::categorize, toList()),
                                TreeMap::new
                        ));
        final Tuple2<CustomSortString, Function<List<DataRow>, Number>>[] series = all.keySet().stream()
                .map(SummaryPrincipalTimelineChart::convert)
                .toArray((IntFunction<Tuple2<CustomSortString, Function<List<DataRow>, Number>>[]>) Tuple2[]::new);
        abstractOriginationTimeline(all.values().stream().flatMap(Collection::stream), adder, series);
    }

    @Override
    public StackingType getStacking() {
        return StackingType.ABSOLUTE;
    }

    @Override
    public boolean isRatingsAsSeries() {
        return false;
    }

    @Override
    public ChartType getType() {
        return ChartType.COLUMN;
    }

    @Override
    public String getLabelForY() {
        return "Výše úvěru";
    }

    @Override
    public String getLabelForZ() {
        return "Podíl počtu na celku [%]";
    }

    @Override
    public String getTitle() {
        return "Skladba výší úvěru v čase";
    }
}
