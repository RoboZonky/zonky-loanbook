package com.github.robozonky.loanbook.input;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataProcessor {

    private static final Logger LOGGER = LogManager.getLogger(Data.class);
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[\\s\\h]+");

    private final Map<String, YearMonth> yearMonthCache = new HashMap<>();
    private final List<DataRow> rows = new ArrayList<>();

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
            return Money.getInstance(cleaned);
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

    private static Insurance toInsurance(final String value) {
        switch (value) {
            case "Ano":
                return Insurance.YES;
            case "V minulosti":
                return Insurance.IN_THE_PAST;
            case "Ne":
                return Insurance.NO;
            default:
                throw new IllegalStateException("Wrong boolean value:" + value);
        }
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

    private static String deduplicate(final String string) {
        return string.intern();
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
                return Ratio.getInstance(result.scaleByPowerOfTen(-2));
            }
        } else {
            throw new IllegalStateException("Wrong ratio: " + value);
        }
    }

    public void add(final String[] sheetRow) {
        try {
            var row = new DataRow(toYearMonth(sheetRow[0]),
                    deduplicate(sheetRow[1]),
                    deduplicate(sheetRow[2]),
                    deduplicate(sheetRow[3]),
                    toBoolean(sheetRow[4]),
                    deduplicate(sheetRow[5]),
                    toRatio(sheetRow[6]),
                    toMoney(sheetRow[7]),
                    toYearMonth(sheetRow[8]),
                    toInt(sheetRow[9]),
                    toInsurance(sheetRow[10]),
                    deduplicate(sheetRow[11]),
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
                    toInt(sheetRow[33]),
                    toYearMonth(sheetRow[34]),
                    deduplicate(sheetRow[35]),
                    sheetRow.length > 36 ? toYearMonth(sheetRow[36]) : null);
            rows.add(row);
        } catch (final Exception ex) {
            LOGGER.warn("Failed processing {}.", sheetRow, ex);
        }
    }

    public List<DataRow> getRows() {
        return rows;
    }

    private YearMonth toYearMonth(final String value) {
        if (value.trim().isEmpty()) {
            return null;
        }
        return yearMonthCache.computeIfAbsent(value, v -> {
            final int[] parts = Arrays.stream(value.split("/"))
                    .mapToInt(Integer::valueOf)
                    .toArray();
            return YearMonth.of(parts[0], parts[1]);
        });
    }
}
