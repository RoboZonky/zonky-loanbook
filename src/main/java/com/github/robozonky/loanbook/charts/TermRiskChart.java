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
        final int start = (cycle * step);
        final int end = ((cycle + 1) * step);
        if (cycle == 0) {
            return new CustomSortString("do " + end, cycle);
        } else if (cycle > 12) {
            return new CustomSortString("od " + start, cycle);
        } else {
            return new CustomSortString("od " + start + " do " + end, cycle);
        }
    }

    @Override
    public String getLabelForX() {
        return "Délka úvěru [měs.]";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle délky úvěru";
    }
}
