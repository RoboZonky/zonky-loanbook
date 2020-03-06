package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import io.vavr.Tuple;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public final class NormalizedTermRiskChart extends AbstractRiskXYZChart {

    public NormalizedTermRiskChart(final Data data) {
        super(data, NormalizedTermRiskChart::termRiskChart);
    }

    private static void termRiskChart(final Stream<DataRow> data,
            final XYZChartDataConsumer adder) {
        final Stream<DataRow> updatedData = data.filter(AbstractRiskXYZChart::filterForFinished);
        final TreeMap<Ratio, TreeMap<Integer, List<DataRow>>> byInterestRateAndTerm =
                updatedData.collect(
                        collectingAndThen(
                                groupingBy(DataRow::getInterestRate,
                                        collectingAndThen(
                                                groupingBy(DataRow::getOriginalInstalmentCount, toList()),
                                                TreeMap::new)),
                                TreeMap::new
                        ));
        // count totals
        final Map<Ratio, Map<Integer, LongAdder>> totals = new HashMap<>(0);
        final Map<Ratio, Map<Integer, LongAdder>> defaultedTotals = new HashMap<>(0);
        byInterestRateAndTerm.forEach((ratio, sub) -> sub.forEach((term, rows) -> {
            totals.computeIfAbsent(ratio, __ -> new HashMap<>(0))
                    .computeIfAbsent(term, __ -> new LongAdder())
                    .add(rows.size());
            final long defaulted = rows.stream().filter(DataRow::isDefaulted).count();
            defaultedTotals.computeIfAbsent(ratio, __ -> new HashMap<>(0))
                    .computeIfAbsent(term, __ -> new LongAdder())
                    .add(defaulted);
        }));
        // sum total defaults per the second parameter
        final Map<Integer, LongAdder> defaultedPerSecondParameter = new HashMap<>(0);
        defaultedTotals.forEach((__, sub) -> sub.forEach((term, result) -> {
            defaultedPerSecondParameter.computeIfAbsent(term, ___ -> new LongAdder()).add(result.longValue());
        }));
        final Map<Integer, LongAdder> totalPerSecondParameter = new HashMap<>(0);
        totals.forEach((__, sub) -> sub.forEach((term, result) -> {
            totalPerSecondParameter.computeIfAbsent(term, ___ -> new LongAdder()).add(result.longValue());
        }));
        // figure out every possible category, in expected order
        final SortedSet<Integer> everyTerm = totals.entrySet().stream()
                .flatMap(entry -> entry.getValue().keySet().stream())
                .collect(toCollection(TreeSet::new));
        // figure out the normalized ratio
        final Map<Ratio, Map<Integer, Double>> normalized = new LinkedHashMap<>(0);
        byInterestRateAndTerm.forEach((rating, sub) -> everyTerm.forEach(term -> {
            final long totalCount = totalPerSecondParameter.getOrDefault(term, new LongAdder()).longValue();
            if (totalCount == 0) {
                normalized.computeIfAbsent(rating, __ -> new LinkedHashMap<>(0)).put(term, 0.0);
            } else {
                final long defaultedCount = defaultedTotals.getOrDefault(rating, Collections.emptyMap())
                        .getOrDefault(term, new LongAdder())
                        .longValue();
                if (defaultedCount == 0) {
                    normalized.computeIfAbsent(rating, __ -> new LinkedHashMap<>(0)).put(term, 0.0);
                } else {
                    final double preNormalized = BigDecimal.valueOf(defaultedCount)
                            .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_EVEN)
                            .doubleValue();
                    final double result = normalizeDefaultRate(term, preNormalized);
                    normalized.computeIfAbsent(rating, __ -> new LinkedHashMap<>(0)).put(term, result);
                }
            }
        }));
        // and categorize
        final Map<Ratio, SortedMap<CustomSortString, List<Double>>> normalizedCategorized = new LinkedHashMap<>(0);
        normalized.forEach((rating, sub) -> {
            final SortedMap<CustomSortString, List<Double>> result = sub.entrySet().stream()
                    .collect(
                            groupingBy(e -> TermRiskChart.getCategory(e.getKey()), TreeMap::new,
                                    mapping(Map.Entry::getValue, toList())));
            normalizedCategorized.put(rating, result);
        });
        // and finally add to the chart
        normalizedCategorized.forEach((rating, sub) -> sub.forEach((term, ratios) -> {
            Collections.sort(ratios);
            final int size = ratios.size();
            double median;
            if (size % 2 == 1) {
                median = ratios.get(size / 2);
            } else {
                final double a = ratios.get(size / 2);
                final double b = ratios.get(size / 2 - 1);
                median = (a + b) / 2;
            }
            adder.accept(Tuple.of(term.toString(), rating + " p.a.", median * 100.0));
        }));
    }

    private static double normalizeDefaultRate(final int currentPeriodInMonths, final double rate) {
        return (rate / currentPeriodInMonths) * 12; // Probability of default per year.
    }


    @Override
    public String getLabelForZ() {
        return "Odhad zesplatněných ročně [%]";
    }

    @Override
    public String getLabelForX() {
        return "Délka úvěru";
    }

    @Override
    public String getTitle() {
        return "Normalizovaná rizikovost podle délky úvěru";
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of("Půjčka na 6 měsíců v absolutních číslech může být bezpečnější, než půjčka na 84 měsíců. " +
                "Ale během těch 84 měsíců dostanete do portfolia 14 různých půjček na 6 měsíců; " +
                "pravděpodobnost zesplatnění alespoň jedné z nich tedy roste " +
                "a postupně převýší pravděpodobnost zesplatnění jedné půjčky na 84 měsíců.");
    }
}
