package com.github.robozonky.loanbook.charts;

import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class PrincipalRiskChart extends AbstractRiskXYZChart {

    public PrincipalRiskChart(final Data data) {
        super(data, PrincipalRiskChart::principalRiskChart);
    }

    private static void principalRiskChart(final Stream<DataRow> data,
                                           final XYZChartDataConsumer adder) {
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

    @Override
    public String getLabelForX() {
        return "Výše úvěru [tis. Kč]";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle výše úvěru";
    }
}
