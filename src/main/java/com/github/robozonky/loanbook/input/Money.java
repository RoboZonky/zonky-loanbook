package com.github.robozonky.loanbook.input;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Money extends Number implements Comparable<Money> {

    private static final Map<String, Money> CACHE = new HashMap<>();

    public static Money getInstance(final String value) {
        return CACHE.computeIfAbsent(value, v -> new Money(new BigDecimal(v)));
    }

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal value;

    private Money(final BigDecimal bigDecimal) {
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
        final Money money = (Money) o;
        return Objects.equals(value, money.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toPlainString() + " Kč";
    }

    @Override
    public int compareTo(final Money money) {
        return this.value.compareTo(money.value);
    }

}
