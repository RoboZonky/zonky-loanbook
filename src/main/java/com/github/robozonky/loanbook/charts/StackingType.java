package com.github.robozonky.loanbook.charts;

public enum StackingType {

    NONE("false"),
    ABSOLUTE("true");

    private final String googleCode;

    StackingType(final String googleCode) {
        this.googleCode = googleCode;
    }

    public String getGoogleCode() {
        return googleCode;
    }
}
