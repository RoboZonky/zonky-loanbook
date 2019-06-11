package com.github.robozonky.loanbook.input;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.vavr.Lazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Data {

    private static final Logger LOGGER = LogManager.getLogger(Data.class);

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s\\h]+");
    private final Set<DataRow> rows = new CopyOnWriteArraySet<>();
    private final Lazy<YearMonth> yearMonth = Lazy.of(() -> getAll().map(DataRow::getReportDate)
            .max(YearMonth::compareTo)
            .orElse(YearMonth.from(LocalDate.now())));

    public static Data process(final String[][] sheet) {
        final Data data = new Data();
        IntStream.range(1, sheet.length)
                .parallel()
                .mapToObj(i -> sheet[i])
                .map(sheetRow -> {
                    try {
                        return new DataRow(toYearMonth(sheetRow[0]),
                                           sheetRow[1],
                                           sheetRow[2],
                                           sheetRow[3],
                                           toBoolean(sheetRow[4]),
                                           sheetRow[5],
                                           toRatio(sheetRow[6]),
                                           toMoney(sheetRow[7]),
                                           toYearMonth(sheetRow[8]),
                                           toInt(sheetRow[9]),
                                           toBoolean(sheetRow[10]),
                                           sheetRow[11],
                                           toMoney(sheetRow[12]),
                                           toInt(sheetRow[13]),
                                           toInt(sheetRow[14]),
                                           toMoney(sheetRow[15]),
                                           toYearMonth(sheetRow[16]),
                                           toInt(sheetRow[17]),
                                           toInt(sheetRow[18]),
                                           toMoney(sheetRow[19]),
                                           toBoolean(sheetRow[20]),
                                           toInt(sheetRow[21]),
                                           toInt(sheetRow[22]),
                                           toInt(sheetRow[23]),
                                           toRatio(sheetRow[24]),
                                           toYearMonth(sheetRow[25]),
                                           toMoney(sheetRow[26]),
                                           toMoney(sheetRow[27]),
                                           toMoney(sheetRow[28]),
                                           toMoney(sheetRow[29]),
                                           toMoney(sheetRow[30]),
                                           toYearMonth(sheetRow[31]),
                                           toBoolean(sheetRow[32]),
                                           toInt(sheetRow[33]));
                    } catch (final Exception ex) {
                        LOGGER.warn("Failed processing {}.", sheetRow, ex);
                        return null;
                    }
                }).forEach(data::addRow);
        LOGGER.info("Loaded {} data rows.", data.getAll().count());
        return data;
    }

    private static double toDouble(final String value) {
        if (value.trim().isEmpty()) {
            return 0;
        } else {
            final String cleaned = cleanupDecimal(value);
            return Double.parseDouble(cleaned);
        }
    }

    private static Money toMoney(final String value) {
        final String cleaned = cleanupDecimal(value);
        if (cleaned.isEmpty()) {
            return Money.ZERO;
        }
        final BigDecimal actual = new BigDecimal(cleaned);
        if (actual.signum() == 0) {
            return Money.ZERO;
        } else {
            return new Money(actual);
        }
    }

    private static boolean toBoolean(final String value) {
        switch (value) {
            case "Ano":
                return true;
            case "Ne":
            case "":
                return false;
            default:
                throw new IllegalStateException("Wrong boolean value:" + value);
        }
    }

    private static YearMonth toYearMonth(final String value) {
        if (value.trim().isEmpty()) {
            return null;
        }
        final int[] parts = Arrays.stream(value.split("/"))
                .mapToInt(Integer::valueOf)
                .toArray();
        return YearMonth.of(parts[0], parts[1]);
    }

    private static int toInt(final String value) {
        return (int) toDouble(value);
    }

    private static String cleanupDecimal(final String value) {
        return WHITESPACE_PATTERN.matcher(value)
                .replaceAll("")
                .replace(",", ".")
                .replace("Kč", "")
                .trim();
    }

    private static Ratio toRatio(final String value) {
        final String actual = value.trim();
        if (actual.isEmpty()) {
            return Ratio.ZERO;
        } else if (actual.contains("%")) {
            final String[] parts = cleanupDecimal(value).split("\\Q%\\E");
            final String decimal = parts[0];
            final BigDecimal result = new BigDecimal(decimal);
            if (result.signum() == 0) {
                return Ratio.ZERO;
            } else {
                return new Ratio(result.divide(BigDecimal.TEN).divide(BigDecimal.TEN));
            }
        } else {
            throw new IllegalStateException("Wrong ratio: " + value);
        }
    }

    private void addRow(final DataRow row) {
        rows.add(row);
    }

    public YearMonth getYearMonth() {
        return yearMonth.get();
    }

    public Stream<DataRow> getAll() {
        return rows.stream()
                .filter(r -> !r.getIncomeType().equals("Nezaměstnaný")); // statistical fluke, no longer used; 7 items
    }
}
