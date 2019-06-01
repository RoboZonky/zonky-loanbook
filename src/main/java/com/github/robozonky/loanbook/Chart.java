package com.github.robozonky.loanbook;

import java.util.concurrent.atomic.AtomicInteger;

abstract class Chart {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private final int id;
    private final String title;

    protected Chart(final String title) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
