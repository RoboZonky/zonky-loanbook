package com.github.robozonky.loanbook.input;

import java.time.YearMonth;
import java.util.Optional;
import java.util.StringJoiner;

public final class DataRow {

    private final YearMonth reportDate;
    private final String userId;
    private final String region;
    private final String incomeType;
    private final boolean additionalIncome;
    private final String purpose;
    private final Ratio interestRate;
    private final Money amount;
    private final YearMonth origin;
    private final int loanCount;
    private final Insurance insured;
    private final String status;
    private final Money paidEarlyAmount;
    private final int postponedInstalmentCount;
    private final int daysPastDue;
    private final Money amountOverdue;
    private final YearMonth lastDelinquent;
    private final int maxDaysPastDue;
    private final int lateInstalmentCount;
    private final Money maxAmountOverdue;
    private final boolean defaulted;
    private final int originalInstalmentCount;
    private final int currentInstalmentCount;
    private final int remainingInstalmentCount;
    private final Ratio lost;
    private final YearMonth finished;
    private final Money principalPaidAmount;
    private final Money interestPaidAmount;
    private final Money penaltyPaidAmount;
    private final Money principalRemainingAmount;
    private final Money interestRemainingAmount;
    private final YearMonth becameInvestor;
    private final boolean story;
    private final int investmentCount;
    private final YearMonth expectedFirstPaymentMonth;
    private final String label;
    private final YearMonth monthOfDefault;

    DataRow(final YearMonth reportDate, final String userId, final String region, final String incomeType,
            final boolean additionalIncome, final String purpose, final Ratio interestRate, final Money amount,
            final YearMonth origin, final int loanCount, final Insurance insured, final String status,
            final Money paidEarlyAmount, final int postponedInstalmentCount, final int daysPastDue,
            final Money amountOverdue, final YearMonth lastDelinquent, final int maxDaysPastDue,
            final int lateInstalmentCount, final Money maxAmountOverdue, final boolean defaulted,
            final int originalInstalmentCount, final int currentInstalmentCount, final int remainingInstalmentCount,
            final Ratio lost, final YearMonth finished, final Money principalPaidAmount, final Money interestPaidAmount,
            final Money penaltyPaidAmount, final Money principalRemainingAmount, final Money interestRemainingAmount,
            final YearMonth becameInvestor, final boolean story, final int investmentCount,
            final YearMonth expectedFirstPaymentMonth, final String label, final YearMonth monthOfDefault) {
        this.reportDate = reportDate;
        this.userId = userId;
        this.region = region;
        this.incomeType = incomeType;
        this.additionalIncome = additionalIncome;
        this.purpose = purpose;
        this.interestRate = interestRate;
        this.amount = amount;
        this.origin = origin;
        this.loanCount = loanCount;
        this.insured = insured;
        this.status = status;
        this.paidEarlyAmount = paidEarlyAmount;
        this.postponedInstalmentCount = postponedInstalmentCount;
        this.daysPastDue = daysPastDue;
        this.amountOverdue = amountOverdue;
        this.lastDelinquent = lastDelinquent;
        this.maxDaysPastDue = maxDaysPastDue;
        this.lateInstalmentCount = lateInstalmentCount;
        this.maxAmountOverdue = maxAmountOverdue;
        this.defaulted = defaulted;
        this.originalInstalmentCount = originalInstalmentCount;
        this.currentInstalmentCount = currentInstalmentCount;
        this.remainingInstalmentCount = remainingInstalmentCount;
        this.lost = lost;
        this.finished = finished;
        this.principalPaidAmount = principalPaidAmount;
        this.interestPaidAmount = interestPaidAmount;
        this.penaltyPaidAmount = penaltyPaidAmount;
        this.principalRemainingAmount = principalRemainingAmount;
        this.interestRemainingAmount = interestRemainingAmount;
        this.becameInvestor = becameInvestor;
        this.story = story;
        this.investmentCount = investmentCount;
        this.expectedFirstPaymentMonth = expectedFirstPaymentMonth;
        this.label = label;
        this.monthOfDefault = monthOfDefault;
    }

    public YearMonth getReportDate() {
        return reportDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getRegion() {
        return region;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public boolean isAdditionalIncome() {
        return additionalIncome;
    }

    public String getPurpose() {
        return purpose;
    }

    public Ratio getInterestRate() {
        return interestRate;
    }

    public Money getAmount() {
        return amount;
    }

    public YearMonth getOrigin() {
        return origin;
    }

    public int getLoanCount() {
        return loanCount;
    }

    public boolean isInsured() {
        return insured == Insurance.YES;
    }

    public String getStatus() {
        return status;
    }

    public Money getPaidEarlyAmount() {
        return paidEarlyAmount;
    }

    public int getPostponedInstalmentCount() {
        return postponedInstalmentCount;
    }

    public int getDaysPastDue() {
        return daysPastDue;
    }

    public Money getAmountOverdue() {
        return amountOverdue;
    }

    public Optional<YearMonth> getLastDelinquent() {
        return Optional.ofNullable(lastDelinquent);
    }

    public int getMaxDaysPastDue() {
        return maxDaysPastDue;
    }

    public int getLateInstalmentCount() {
        return lateInstalmentCount;
    }

    public Money getMaxAmountOverdue() {
        return maxAmountOverdue;
    }

    public boolean isDefaulted() {
        return defaulted;
    }

    public int getOriginalInstalmentCount() {
        return originalInstalmentCount;
    }

    public int getCurrentInstalmentCount() {
        return currentInstalmentCount;
    }

    public int getRemainingInstalmentCount() {
        return remainingInstalmentCount;
    }

    public Ratio getLost() {
        return lost;
    }

    public Optional<YearMonth> getFinished() {
        return Optional.ofNullable(finished);
    }

    public Money getPrincipalPaidAmount() {
        return principalPaidAmount;
    }

    public Money getInterestPaidAmount() {
        return interestPaidAmount;
    }

    public Money getPenaltyPaidAmount() {
        return penaltyPaidAmount;
    }

    public Money getPrincipalRemainingAmount() {
        return principalRemainingAmount;
    }

    public Money getInterestRemainingAmount() {
        return interestRemainingAmount;
    }

    public Optional<YearMonth> getBecameInvestor() {
        return Optional.ofNullable(becameInvestor);
    }

    public boolean isStory() {
        return story;
    }

    public int getInvestmentCount() {
        return investmentCount;
    }

    public YearMonth getExpectedFirstPaymentMonth() {
        return expectedFirstPaymentMonth;
    }

    public String getLabel() {
        return label;
    }

    public YearMonth getMonthOfDefault() {
        return monthOfDefault;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DataRow.class.getSimpleName() + "[", "]")
                .add("additionalIncome=" + additionalIncome)
                .add("amount=" + amount)
                .add("amountOverdue=" + amountOverdue)
                .add("becameInvestor=" + becameInvestor)
                .add("currentInstalmentCount=" + currentInstalmentCount)
                .add("daysPastDue=" + daysPastDue)
                .add("defaulted=" + defaulted)
                .add("expectedFirstPaymentMonth=" + expectedFirstPaymentMonth)
                .add("finished=" + finished)
                .add("incomeType='" + incomeType + "'")
                .add("insured=" + insured)
                .add("interestPaidAmount=" + interestPaidAmount)
                .add("interestRate=" + interestRate)
                .add("interestRemainingAmount=" + interestRemainingAmount)
                .add("investmentCount=" + investmentCount)
                .add("label='" + label + "'")
                .add("lastDelinquent=" + lastDelinquent)
                .add("lateInstalmentCount=" + lateInstalmentCount)
                .add("loanCount=" + loanCount)
                .add("lost=" + lost)
                .add("maxAmountOverdue=" + maxAmountOverdue)
                .add("maxDaysPastDue=" + maxDaysPastDue)
                .add("monthOfDefault=" + monthOfDefault)
                .add("origin=" + origin)
                .add("originalInstalmentCount=" + originalInstalmentCount)
                .add("paidEarlyAmount=" + paidEarlyAmount)
                .add("penaltyPaidAmount=" + penaltyPaidAmount)
                .add("postponedInstalmentCount=" + postponedInstalmentCount)
                .add("principalPaidAmount=" + principalPaidAmount)
                .add("principalRemainingAmount=" + principalRemainingAmount)
                .add("purpose='" + purpose + "'")
                .add("region='" + region + "'")
                .add("remainingInstalmentCount=" + remainingInstalmentCount)
                .add("reportDate=" + reportDate)
                .add("status='" + status + "'")
                .add("story=" + story)
                .add("userId='" + userId + "'")
                .toString();
    }
}
