package com.github.robozonky.loanbook.charts;

import com.github.robozonky.loanbook.input.Data;

public class VintageDefaultRatesTimelineChart extends AbstractDefaultRatesTimelineChart {

    public VintageDefaultRatesTimelineChart(final Data data) {
        super(data, d -> new CustomSortString("" + d.getOrigin().getYear(), d.getOrigin().getYear()));
    }

    @Override
    public boolean isRatingsAsSeries() {
        return false;
    }

    @Override
    public String getLabelForY() {
        return "Rok originace půjčky";
    }

    @Override
    public String getTitle() {
        return "Zesplatnění v průběhu života půjčky, podle roku originace";
    }
}
