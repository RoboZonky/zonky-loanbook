package com.github.robozonky.loanbook.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Data {

    private static final Logger LOGGER = LogManager.getLogger(Data.class);

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s\\h]+");
    private final Set<DataRow> rows = new CopyOnWriteArraySet<>();

    public static Data process(final String[][] sheet) {
        final Data data = new Data();
        IntStream.range(1, sheet.length)
                .parallel()
                .mapToObj(i -> sheet[i])
                .map(sheetRow -> {
                    try {
                        return new DataRow(sheetRow[0],
                                           sheetRow[1],
                                           sheetRow[2],
                                           sheetRow[3],
                                           toRatio(sheetRow[4]),
                                           toMoney(sheetRow[5]),
                                           toLocalDate(sheetRow[6]),
                                           toInt(sheetRow[7]),
                                           toBoolean(sheetRow[8]),
                                           sheetRow[9],
                                           toMoney(sheetRow[10]),
                                           toInt(sheetRow[11]),
                                           toInt(sheetRow[12]),
                                           toMoney(sheetRow[13]),
                                           toYearMonth(sheetRow[14]),
                                           toInt(sheetRow[15]),
                                           toInt(sheetRow[16]),
                                           toMoney(sheetRow[17]),
                                           toBoolean(sheetRow[18]),
                                           toInt(sheetRow[19]),
                                           toInt(sheetRow[20]),
                                           toInt(sheetRow[21]),
                                           toRatio(sheetRow[22]),
                                           toYearMonth(sheetRow[23]),
                                           toMoney(sheetRow[24]),
                                           toMoney(sheetRow[25]),
                                           toMoney(sheetRow[26]),
                                           toMoney(sheetRow[27]),
                                           toMoney(sheetRow[28]),
                                           toLocalDate(sheetRow[29]),
                                           toBoolean(sheetRow[30]),
                                           toInt(sheetRow[31]));
                    } catch (final Exception ex) {
                        LOGGER.warn("Failed processing {}.", sheetRow, ex);
                        return null;
                    }
                }).forEach(data::addRow);
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
        return YearMonth.from(toLocalDate(value));
    }

    private static LocalDate toLocalDate(final String value) {
        if (value.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private static int toInt(final String value) {
        return (int) toDouble(value);
    }

    private static String cleanupDecimal(final String value) {
        return WHITESPACE_PATTERN.matcher(value)
                .replaceAll("")
                .replace(",", ".");
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
}
