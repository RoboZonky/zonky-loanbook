package com.github.robozonky.loanbook.charts;

import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class InterestRateDefaultTimelineChart extends AbstractTimelineXYZChart {

    public InterestRateDefaultTimelineChart(final Data data) {
        super(data, InterestRateDefaultTimelineChart::interestRateDefaultTimeline);
    }

    private static void interestRateDefaultTimeline(final Stream<DataRow> data,
                                                    final XYZChartDataConsumer adder) {
        final Stream<DataRow> updatedData = data.filter(AbstractRiskXYZChart::filterForFinished);
        abstractInterestRateHealthTimeline(updatedData, DataRow::isDefaulted, adder);
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
        return "Zesplatněno z ukončených [%]";
    }

    @Override
    public String getTitle() {
        return "Zesplatnění podle data originace a ratingu [%]";
    }
}
