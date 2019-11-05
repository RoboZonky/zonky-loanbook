package com.github.robozonky.loanbook.charts;

import java.util.Optional;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class PrincipalRiskChart extends AbstractRiskXYZChart {

    public static CustomSortString getCategory(final DataRow r) {
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
    }

    public PrincipalRiskChart(final Data data) {
        super(data, PrincipalRiskChart::principalRiskChart);
    }

    private static void principalRiskChart(final Stream<DataRow> data,
                                           final XYZChartDataConsumer adder) {
        abstractRiskChartCustomSorted(data, PrincipalRiskChart::getCategory, adder);
    }

    @Override
    public String getLabelForX() {
        return "Výše úvěru [tis. Kč]";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle výše úvěru";
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of("První půjčka nad 500 tis. Kč byla poskytnuta na konci r. 2016. Půjčky nad tuto částku tedy mohou být o něco mladší než ostatní.");
    }
}
