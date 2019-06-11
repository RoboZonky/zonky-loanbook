package com.github.robozonky.loanbook.charts;

import java.util.Optional;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class PreviousLoansRiskChart extends AbstractRiskXYZChart {

    public PreviousLoansRiskChart(final Data data) {
        super(data, PreviousLoansRiskChart::previousLoansRiskChart);
    }

    private static void previousLoansRiskChart(final Stream<DataRow> data,
                                               final XYZChartDataConsumer adder) {
        abstractRiskChartCustomSorted(data, r -> new CustomSortString("" + (r.getLoanCount() - 1), r.getLoanCount()),
                                      adder);
    }

    @Override
    public String getLabelForX() {
        return "Počet předchozích půjček";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle počtu předchozích půjček";
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of(
                "Započítány jsou všechny dříve originované Zonky půjčky klienta nezávisle na tom, zda už doběhly.");
    }
}
