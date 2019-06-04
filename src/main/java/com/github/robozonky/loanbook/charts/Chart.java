package com.github.robozonky.loanbook.charts;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Chart {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private final int id;
    private final int axisCount;
    private final ChartType type;
    private final String title;
    private final String subtitle;

    protected Chart(final ChartType type, final int axisCount, final String title, final String subtitle) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.axisCount = axisCount;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getId() {
        return id;
    }

    public int getAxisCount() {
        return axisCount;
    }

    public ChartType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
