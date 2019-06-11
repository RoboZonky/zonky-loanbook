package com.github.robozonky.loanbook.charts;

import java.util.Optional;

public interface Chart {

    int getId();

    int getAxisCount();

    ChartType getType();

    String getTitle();

    String getSubtitle();

    default Optional<String> getComment() {
        return Optional.empty();
    }
}
