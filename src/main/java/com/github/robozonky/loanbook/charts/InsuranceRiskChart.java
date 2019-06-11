package com.github.robozonky.loanbook.charts;

import java.time.YearMonth;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class InsuranceRiskChart extends AbstractRiskXYZChart {

    public InsuranceRiskChart(final Data data) {
        super(data, InsuranceRiskChart::interestRateInsuranceRiskChart);
    }

    private static void interestRateInsuranceRiskChart(final Stream<DataRow> data,
                                                       final XYZChartDataConsumer adder) {
        abstractInterestRateHealthBinary(data, DataRow::isDefaulted, DataRow::isInsured, adder);
    }

    @Override
    public String getLabelForX() {
        return "Má pojištění?";
    }

    @Override
    public Stream<DataRow> getApplicableDataRows() {
        final YearMonth insuranceStart = YearMonth.of(2018, 4);
        return super.getApplicableDataRows().filter(r -> YearMonth.from(r.getOrigin()).isAfter(insuranceStart));
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle pojištění, od jeho zavedení";
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of("Pojištění bylo zavedeno teprve v dubnu 2018, půjčky z předchozího období nejsou zahrnuty.");
    }
}
