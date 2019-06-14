package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import io.vavr.Tuple;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class AverageInvestmentTimelineChart extends AbstractTimelineXYZChart {

    public AverageInvestmentTimelineChart(final Data data) {
        super(data, AverageInvestmentTimelineChart::interestRateInvestmentSizeTimeline);
    }

    private static void interestRateInvestmentSizeTimeline(final Stream<DataRow> data,
                                                           final XYZChartDataConsumer adder) {
        final TreeMap<YearMonth, TreeMap<Ratio, List<DataRow>>> byMonthAndRating =
                data.collect(
                        collectingAndThen(
                                groupingBy(r -> YearMonth.from(r.getOrigin()),
                                           collectingAndThen(
                                                   groupingBy(DataRow::getInterestRate,
                                                              toList()),
                                                   TreeMap::new)),
                                TreeMap::new
                        ));
        // count totals
        final Map<YearMonth, Map<Ratio, Long>> loanCounts = new HashMap<>(0);
        final Map<YearMonth, Map<Ratio, Long>> investorCounts = new HashMap<>(0);
        byMonthAndRating.forEach((yearMonth, sub) -> sub.forEach((ratio, rows) -> {
            loanCounts.computeIfAbsent(yearMonth, __ -> new HashMap<>())
                    .computeIfAbsent(ratio, __ -> rows.stream().count());
            investorCounts.computeIfAbsent(yearMonth, __ -> new HashMap<>())
                    .computeIfAbsent(ratio, __ -> rows.stream().mapToLong(DataRow::getInvestmentCount).sum());
        }));
        // figure out every possible category, in expected order
        final SortedSet<Ratio> everySecond = loanCounts.entrySet().stream()
                .flatMap(entry -> entry.getValue().keySet().stream())
                .collect(toCollection(TreeSet::new));
        // figure out the ratio and store into the chart
        byMonthAndRating.forEach((yearMonth, sub) -> everySecond.forEach(second -> {
            final long loans = loanCounts.getOrDefault(yearMonth, Collections.emptyMap())
                    .getOrDefault(second, 0l);
            final long investors = investorCounts.getOrDefault(yearMonth, Collections.emptyMap())
                    .getOrDefault(second, 0l);
            final int result = loans == 0 ?
                    0 :
                    BigDecimal.valueOf(investors)
                            .divide(BigDecimal.valueOf(loans), 4, RoundingMode.HALF_EVEN)
                            .intValue();
            adder.accept(Tuple.of(yearMonth.toString(), second + " p.a.", result));
        }));
    }

    @Override
    public boolean isRatingsAsSeries() {
        return true;
    }

    @Override
    public String getLabelForX() {
        return "Datum originace";
    }

    @Override
    public String getLabelForY() {
        return "Úroková míra [% p.a.]";
    }

    @Override
    public String getLabelForZ() {
        return "Průměrný počet investorů";
    }

    @Override
    public String getTitle() {
        return "Průměrný počet investorů v jedné půjčce";
    }
}
