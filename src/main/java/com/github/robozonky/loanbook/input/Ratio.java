package com.github.robozonky.loanbook.input;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public final class Ratio extends Number implements Comparable<Ratio> {

    public static final Ratio ZERO = new Ratio(BigDecimal.ZERO);
    private static final DecimalFormatSymbols FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.forLanguageTag("cs_CZ"));
    private static final DecimalFormat FORMAT = new DecimalFormat("##.## %", FORMAT_SYMBOLS);
    private final BigDecimal value;

    Ratio(final BigDecimal bigDecimal) {
        this.value = bigDecimal;
    }

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !Objects.equals(getClass(), o.getClass())) {
            return false;
        }
        final Ratio ratio = (Ratio) o;
        return Objects.equals(value, ratio.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return FORMAT.format(value);
    }

    @Override
    public int compareTo(final Ratio ratio) {
        return this.value.compareTo(ratio.value);
    }
}
