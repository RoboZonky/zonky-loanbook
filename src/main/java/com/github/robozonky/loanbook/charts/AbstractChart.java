package com.github.robozonky.loanbook.charts;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public abstract class AbstractChart implements Chart {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private final int id;
    private final Data data;

    protected AbstractChart(final Data data) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.data = data;
    }

    public int getId() {
        return id;
    }

    protected Stream<DataRow> getApplicableDataRows() {
        return data.getAll();
    }

    @Override
    public String getSubtitle() {
        var localDate = LocalDate.now();
        var formatter = DateTimeFormatter.ofPattern("d. M. yyyy");
        var formattedDate = localDate.format(formatter);
        var loanBookDate = data.getYearMonth().getMonthValue() + "/" + data.getYearMonth().getYear();
        return "Vygeneroval Lukáš Petrovický dne " + formattedDate + " ze Zonky loanbooku k " + loanBookDate + '.';
    }
}
