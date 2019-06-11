package com.github.robozonky.loanbook.charts;

import java.util.Optional;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class InvestorRiskChart extends AbstractRiskXYZChart {

    public InvestorRiskChart(final Data data) {
        super(data, InvestorRiskChart::interestRateInvestorRiskChart);
    }

    private static void interestRateInvestorRiskChart(final Stream<DataRow> data,
                                                      final XYZChartDataConsumer adder) {
        abstractInterestRateHealthBinary(data, DataRow::isDefaulted, r -> r.getBecameInvestor()
                                                 .map(became -> became.isBefore(r.getOrigin()) || became.equals(r.getOrigin()))
                                                 .orElse(false),
                                         adder);
    }

    @Override
    public String getLabelForX() {
        return "Byl při originaci investorem?";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle statusu investora";
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of("Započítány jsou pouze ty půjčky, při jejichž originaci byl klient již investorem.");
    }
}
