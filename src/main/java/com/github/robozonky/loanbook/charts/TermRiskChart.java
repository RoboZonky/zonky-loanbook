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
        return getCategory(r.getOriginalInstalmentCount());
    }

    public static CustomSortString getCategory(final int originalInstalmentCount) {
        final int minimum = 6; // There are no loans shorter than 6 months.
        final int step = 6; // New category every 6 months.
        final int cycle = (originalInstalmentCount - 1 - minimum) / step;
        final int leftBoundInclusive = cycle == 0 ? minimum : (cycle * step) + step + 1;
        final int rightBoundInclusive = ((cycle + 1) * step) + step;
        if (cycle > 11) {
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
