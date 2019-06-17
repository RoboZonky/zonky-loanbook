package com.github.robozonky.loanbook.charts;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;

public class InterestRateDefaultRatesTimelineChart extends AbstractDefaultRatesTimelineChart {

    public InterestRateDefaultRatesTimelineChart(final Data data) {
        super(data, InterestRateDefaultRatesTimelineChart::toSortable);
    }

    private static CustomSortString toSortable(final DataRow row) {
        final Ratio rate = row.getInterestRate();
        final int asInt = (int)(rate.doubleValue() * 100);
        return new CustomSortString(rate + " p.a.", asInt);
    }

    @Override
    public boolean isRatingsAsSeries() {
        return true;
    }

    @Override
    public String getLabelForY() {
        return "Úroková míra [% p.a.]";
    }

    @Override
    public String getTitle() {
        return "Zesplatnění v průběhu života půjčky, podle ratingu";
    }
}
