package com.github.robozonky.loanbook.model;

import java.math.BigDecimal;

public final class Ratio extends Number {

    public static final Ratio ZERO = new Ratio(BigDecimal.ZERO);

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
    public String toString() {
        return value.toPlainString();
    }
}
