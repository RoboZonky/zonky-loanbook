package com.github.robozonky.loanbook.charts;

import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class TermRiskChart extends AbstractRiskXYZChart {

    public TermRiskChart(final Data data) {
        super(data, TermRiskChart::termRiskChart);
    }

    private static void termRiskChart(final Stream<DataRow> data,
                                      final XYZChartDataConsumer adder) {
        abstractRiskChartCustomSorted(data, TermRiskChart::getCategory, adder);
    }

    public static CustomSortString getCategory(final DataRow r) {
        final int step = 6;
        final int cycle = (r.getOriginalInstalmentCount() - 1) / step;
        final int leftBoundInclusive = (cycle * step) + 1;
        final int rightBoundInclusive = ((cycle + 1) * step);
        if (cycle == 0) {
            return new CustomSortString("do " + rightBoundInclusive + " měs.", cycle);
        } else if (cycle > 12) {
            return new CustomSortString("od " + leftBoundInclusive + " měs.", cycle);
        } else {
            return new CustomSortString(leftBoundInclusive + " až " + rightBoundInclusive + " měs.", cycle);
        }
    }

    @Override
    public String getLabelForX() {
        return "Délka úvěru";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle délky úvěru";
    }
}
