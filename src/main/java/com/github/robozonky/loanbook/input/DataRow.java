package com.github.robozonky.loanbook.input;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

public final class DataRow {

    private final String userId;
    private final String region;
    private final String incomeType;
    private final String purpose;
    private final Ratio interestRate;
    private final Money amount;
    private final LocalDate origin;
    private final int loanCount;
    private final boolean insured;
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
    private final LocalDate becameInvestor;
    private final boolean story;
    private final int investmentCount;

    DataRow(final String userId, final String region, final String incomeType, final String purpose,
            final Ratio interestRate, final Money amount, final LocalDate origin, final int loanCount,
            final boolean insured, final String status, final Money paidEarlyAmount, final int postponedInstalmentCount,
            final int daysPastDue, final Money amountOverdue, final YearMonth lastDelinquent, final int maxDaysPastDue,
            final int lateInstalmentCount, final Money maxAmountOverdue, final boolean defaulted,
            final int originalInstalmentCount, final int currentInstalmentCount, final int remainingInstalmentCount,
            final Ratio lost, final YearMonth finished, final Money principalPaidAmount, final Money interestPaidAmount,
            final Money penaltyPaidAmount, final Money principalRemainingAmount, final Money interestRemainingAmount,
            final LocalDate becameInvestor, final boolean story, final int investmentCount) {
        this.userId = userId;
        this.region = region;
        this.incomeType = incomeType;
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

    public String getPurpose() {
        return purpose;
    }

    public Ratio getInterestRate() {
        return interestRate;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDate getOrigin() {
        return origin;
    }

    public int getLoanCount() {
        return loanCount;
    }

    public boolean isInsured() {
        return insured;
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

    public Optional<LocalDate> getBecameInvestor() {
        return Optional.ofNullable(becameInvestor);
    }

    public boolean isStory() {
        return story;
    }

    public int getInvestmentCount() {
        return investmentCount;
    }

    @Override
    public String toString() {
        return "DataRow{" +
                "amount=" + amount +
                ", amountOverdue=" + amountOverdue +
                ", becameInvestor=" + becameInvestor +
                ", currentInstalmentCount=" + currentInstalmentCount +
                ", daysPastDue=" + daysPastDue +
                ", defaulted=" + defaulted +
                ", finished=" + finished +
                ", incomeType='" + incomeType + '\'' +
                ", insured=" + insured +
                ", interestPaidAmount=" + interestPaidAmount +
                ", interestRate=" + interestRate +
                ", interestRemainingAmount=" + interestRemainingAmount +
                ", investmentCount=" + investmentCount +
                ", lastDelinquent=" + lastDelinquent +
                ", lateInstalmentCount=" + lateInstalmentCount +
                ", loanCount=" + loanCount +
                ", lost=" + lost +
                ", maxAmountOverdue=" + maxAmountOverdue +
                ", maxDaysPastDue=" + maxDaysPastDue +
                ", origin=" + origin +
                ", originalInstalmentCount=" + originalInstalmentCount +
                ", paidEarlyAmount=" + paidEarlyAmount +
                ", penaltyPaidAmount=" + penaltyPaidAmount +
                ", postponedInstalmentCount=" + postponedInstalmentCount +
                ", principalPaidAmount=" + principalPaidAmount +
                ", principalRemainingAmount=" + principalRemainingAmount +
                ", purpose='" + purpose + '\'' +
                ", region='" + region + '\'' +
                ", remainingInstalmentCount=" + remainingInstalmentCount +
                ", status='" + status + '\'' +
                ", story=" + story +
                ", userId='" + userId + '\'' +
                '}';
    }
}
