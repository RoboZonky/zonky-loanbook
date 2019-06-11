package com.github.robozonky.loanbook.charts;

import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class PurposeRiskChart extends AbstractRiskXYZChart {

    public PurposeRiskChart(final Data data) {
        super(data, PurposeRiskChart::purposeRiskChart);
    }

    private static void purposeRiskChart(final Stream<DataRow> data,
                                         final XYZChartDataConsumer adder) {
        abstractRiskChart(data, DataRow::getPurpose, adder);
    }

    @Override
    public String getLabelForX() {
        return "Účel";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle účelu";
    }
}
