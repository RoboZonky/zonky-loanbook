package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import io.vavr.Tuple;

public final class StoryAndInsuranceTimelineChart extends AbstractTimelineXYZChart {

    public StoryAndInsuranceTimelineChart(final Data data) {
        super(data, StoryAndInsuranceTimelineChart::storyAndInsuranceTimelineChart);
    }

    private static Number somethingToTotalRatio(final List<DataRow> data, final Predicate<DataRow> include) {
        final long stories = data.stream()
                .filter(include)
                .count();
        final BigDecimal ratio = BigDecimal.valueOf(stories)
                .divide(BigDecimal.valueOf(data.size()), 4, RoundingMode.HALF_EVEN);
        return new Ratio(ratio);
    }

    private static Number insuredToTotalRatio(final List<DataRow> data) {
        return somethingToTotalRatio(data, DataRow::isInsured);
    }

    private static Number storiedToTotalRatio(final List<DataRow> data) {
        return somethingToTotalRatio(data, DataRow::isStory);
    }

    private static Number defaultedToTotalRatio(final List<DataRow> data) {
        return somethingToTotalRatio(data, DataRow::isDefaulted);
    }

    private static void storyAndInsuranceTimelineChart(final Stream<DataRow> data,
                                                       final XYZChartDataConsumer adder) {
        abstractTimeline(data, adder,
                         Tuple.of("Zesplatněno [%]", StoryAndInsuranceTimelineChart::defaultedToTotalRatio),
                         Tuple.of("S pojištěním [%]", StoryAndInsuranceTimelineChart::insuredToTotalRatio),
                         Tuple.of("S příběhem [%]", StoryAndInsuranceTimelineChart::storiedToTotalRatio));
    }

    @Override
    public boolean isRatingsAsSeries() {
        return false;
    }

    @Override
    public String getLabelForY() {
        return "";
    }

    @Override
    public String getLabelForZ() {
        return "";
    }

    @Override
    public String getTitle() {
        return "Zesplatnění, příběhy a pojištění podle data originace";
    }
}
