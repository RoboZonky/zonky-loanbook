package com.github.robozonky.loanbook.charts;

import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class RegionRiskChart extends AbstractRiskXYZChart {

    public RegionRiskChart(final Data data) {
        super(data, RegionRiskChart::regionRiskChart);
    }

    private static void regionRiskChart(final Stream<DataRow> data,
                                        final XYZChartDataConsumer adder) {
        abstractRiskChart(data, DataRow::getRegion, adder);
    }

    @Override
    public String getLabelForX() {
        return "Kraj";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle kraje žadatele";
    }
}
