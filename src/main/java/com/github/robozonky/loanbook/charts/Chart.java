package com.github.robozonky.loanbook.charts;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Chart {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private final int id;
    private final ChartType type;
    private final String title;

    protected Chart(final ChartType type, final String title) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.type = type;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public ChartType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
