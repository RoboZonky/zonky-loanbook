package com.github.robozonky.loanbook;

import java.util.Comparator;
import java.util.Objects;

final class CustomSortString implements Comparable<CustomSortString> {

    private static final Comparator<CustomSortString> COMPARATOR =
            Comparator.<CustomSortString>comparingInt(c -> c.sortId).thenComparing(c -> c.parent);
    final int sortId;
    final String parent;

    public CustomSortString(final String parent) {
        this(parent, 0);
    }

    public CustomSortString(final String parent, final int sortId) {
        this.sortId = sortId;
        this.parent = parent;
    }

    @Override
    public int compareTo(final CustomSortString customSortString) {
        return COMPARATOR.compare(this, customSortString);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !Objects.equals(getClass(), o.getClass())) {
            return false;
        }
        final CustomSortString that = (CustomSortString) o;
        return Objects.equals(parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent);
    }

    @Override
    public String toString() {
        return parent;
    }
}
