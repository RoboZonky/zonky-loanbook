package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import io.vavr.Tuple;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

public final class GlobalSummaryTimelineChart extends AbstractTimelineXYZChart {

    public GlobalSummaryTimelineChart(final Data data) {
        super(data, GlobalSummaryTimelineChart::storyAndInsuranceTimelineChart);
    }

    private static Number somethingToTotalRatio(final List<DataRow> data, final Predicate<DataRow> include) {
        final long stories = data.stream()
                .filter(include)
                .count();
        final BigDecimal ratio = BigDecimal.valueOf(stories)
                .divide(BigDecimal.valueOf(data.size()), 4, RoundingMode.HALF_EVEN);
        return new Ratio(ratio);
    }

    private static Number sumWeightedInterestRate(final List<DataRow> data) {
        final Map<Ratio, Long> collect = data.stream()
                .collect(groupingBy(DataRow::getInterestRate,
                                    summingLong(r -> r.getAmount().intValue())));
        final double total = collect.values().stream().mapToLong(i -> i).sum();
        return collect.entrySet().stream()
                .map(e -> e.getKey().doubleValue() * (e.getValue() / total))
                .map(e -> e * 100)
                .mapToDouble(i -> i)
                .sum();
    }

    private static Number countWeightedInterestRate(final List<DataRow> data) {
        final Map<Ratio, Long> collect = data.stream()
                .collect(groupingBy(DataRow::getInterestRate,
                                    counting()));
        final double total = data.size();
        return collect.entrySet().stream()
                .map(e -> e.getKey().doubleValue() * (e.getValue() / total))
                .map(e -> e * 100)
                .mapToDouble(i -> i)
                .sum();
    }

    private static Number insuredToTotalRatio(final List<DataRow> data) {
        return somethingToTotalRatio(data, DataRow::isInsured);
    }

    private static Number lostToTotalRatio(final List<DataRow> data) {
        final double collect = data.stream()
                .mapToDouble(e -> e.getLost().doubleValue() * e.getAmount().doubleValue())
                .sum();
        final double total = data.stream()
                .mapToDouble(e -> e.getAmount().doubleValue())
                .sum();
        return new Ratio(BigDecimal.valueOf(collect / total));
    }

    private static Number unstoriedToTotalRatio(final List<DataRow> data) {
        return somethingToTotalRatio(data, r-> !r.isStory());
    }

    private static Number defaultedToTotalRatio(final List<DataRow> data) {
        return somethingToTotalRatio(data, DataRow::isDefaulted);
    }

    private static void storyAndInsuranceTimelineChart(final Stream<DataRow> data,
                                                       final XYZChartDataConsumer adder) {
        abstractGlobalTimeline(data, adder,
                         Tuple.of("Objemem vážený průměrný úrok [% p.a.]",
                                  GlobalSummaryTimelineChart::sumWeightedInterestRate),
                         Tuple.of("Počtem vážený průměrný úrok [% p.a.]",
                                  GlobalSummaryTimelineChart::countWeightedInterestRate),
                         Tuple.of("Ztraceno [% objemu]", GlobalSummaryTimelineChart::lostToTotalRatio),
                         Tuple.of("Zesplatněno [% půjček]", GlobalSummaryTimelineChart::defaultedToTotalRatio),
                         Tuple.of("S pojištěním [% půjček]", GlobalSummaryTimelineChart::insuredToTotalRatio),
                         Tuple.of("Bez příběhu [% půjček]", GlobalSummaryTimelineChart::unstoriedToTotalRatio));
    }

    @Override
    public boolean isRatingsAsSeries() {
        return false;
    }

    @Override
    public String getLabelForX() {
        return "Měsíc";
    }

    @Override
    public String getLabelForY() {
        return "";
    }

    @Override
    public String getLabelForZ() {
        return "";
    }

    @Override
    public String getTitle() {
        return "Souhrnné statistiky platformy";
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of("Do daného měsíce počítáme všechny v tu dobu aktivní půjčky v platformě.");
    }
}
