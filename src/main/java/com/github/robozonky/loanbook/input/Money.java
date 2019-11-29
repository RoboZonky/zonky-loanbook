package com.github.robozonky.loanbook.input;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Money extends Number implements Comparable<Money> {

    public static final Money ZERO = new Money(BigDecimal.ZERO);
    private static final Map<String, Money> CACHE = new HashMap<>();
    private final int value;

    private Money(final BigDecimal bigDecimal) {
        this.value = bigDecimal.scaleByPowerOfTen(2).intValue();
    }

    public static Money getInstance(final String value) {
        return CACHE.computeIfAbsent(value, v -> new Money(new BigDecimal(v)));
    }

    private static BigDecimal descale(int value) {
        return BigDecimal.valueOf(value).scaleByPowerOfTen(-2);
    }

    @Override
    public int intValue() {
        return value / 100;
    }

    @Override
    public long longValue() {
        return value / 100;
    }

    @Override
    public float floatValue() {
        return descale(value).floatValue();
    }

    @Override
    public double doubleValue() {
        return descale(value).doubleValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !Objects.equals(getClass(), o.getClass())) {
            return false;
        }
        final Money money = (Money) o;
        return Objects.equals(value, money.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return descale(value).toPlainString() + " Kč";
    }

    @Override
    public int compareTo(final Money money) {
        return Integer.compare(value, money.value);
    }
}
