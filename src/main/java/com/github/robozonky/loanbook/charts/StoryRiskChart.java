package com.github.robozonky.loanbook.charts;

import java.util.Optional;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class StoryRiskChart extends AbstractRiskXYZChart {

    public StoryRiskChart(final Data data) {
        super(data, StoryRiskChart::interestRateStoryRiskChart);
    }

    private static void interestRateStoryRiskChart(final Stream<DataRow> data,
                                                   final XYZChartDataConsumer adder) {
        abstractInterestRateHealthBinary(data, DataRow::isDefaulted, DataRow::isStory, adder);
    }

    @Override
    public String getLabelForX() {
        return "Má příběh?";
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle příběhu";
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of("Do r. 2018 byl příběh vyplněn u více než 90 % procent originovaných půjček. Toto číslo následně začalo dramaticky klesat. Je tedy potřeba vzít v úvahu, že půjčky bez příběhu jsou z velké části výrazně mladší než půjčky s příběhem.");
    }
}
