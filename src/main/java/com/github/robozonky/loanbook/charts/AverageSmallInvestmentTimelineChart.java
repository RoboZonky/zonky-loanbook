package com.github.robozonky.loanbook.charts;

import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class AverageSmallInvestmentTimelineChart extends AverageInvestmentTimelineChart {

    public AverageSmallInvestmentTimelineChart(final Data data) {
        super(data);
    }

    @Override
    protected Stream<DataRow> getApplicableDataRows() {
        return super.getApplicableDataRows().filter(r -> r.getAmount().intValue() < 150_000);
    }

    @Override
    public boolean isRatingsAsSeries() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Průměrný počet investorů v jedné půjčce pod 150.000,- Kč";
    }
}
