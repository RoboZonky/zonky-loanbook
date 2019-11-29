package com.github.robozonky.loanbook.input;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

import io.vavr.Lazy;

public final class Data {

    private final List<DataRow> rows;
    private final Lazy<YearMonth> yearMonth = Lazy.of(() -> getAll().map(DataRow::getReportDate)
            .max(YearMonth::compareTo)
            .orElse(YearMonth.from(LocalDate.now())));

    public Data(final List<DataRow> rows) {
        this.rows = rows;
    }

    public YearMonth getYearMonth() {
        return yearMonth.get();
    }

    public Stream<DataRow> getAll() {
        return rows.stream();
    }
}
