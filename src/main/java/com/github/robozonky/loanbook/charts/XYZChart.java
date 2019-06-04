package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;

public class XYZChart extends Chart {

    private final String labelForX;
    private final String labelForY;
    private final String labelForZ;
    private final Set<String> adaptedAxisLabels = new LinkedHashSet<>();
    private final Map<String, Map<String, Number>> adaptedData = new LinkedHashMap<>();
    private final List<Tuple3<String, String, Number>> data = new ArrayList<>(0);
    private final boolean ratingsAsSeries;

    private static boolean hasInterestRate(final String... label) {
        return Arrays.stream(label).anyMatch(s -> s.contains("% p.a."));
    }

    public XYZChart(final ChartType type, final String title, final String subtitle, final String labelForX,
                    final String labelForY, final String labelForZ) {
        this(type, title, subtitle, labelForX, labelForY, labelForZ, hasInterestRate(labelForX, labelForY, labelForZ));
    }

    public XYZChart(final ChartType type, final String title, final String subtitle, final String labelForX,
                    final String labelForY,
                    final String labelForZ, final boolean ratingsAsSeries) {
        super(type, 3, title, subtitle);
        this.labelForX = labelForX;
        this.labelForY = labelForY;
        this.labelForZ = labelForZ;
        this.ratingsAsSeries = ratingsAsSeries;
    }

    public boolean isRatingsAsSeries() {
        return ratingsAsSeries;
    }

    public String getLabelForX() {
        return labelForX;
    }

    public String getLabelForY() {
        return labelForY;
    }

    public String getLabelForZ() {
        return labelForZ;
    }

    public List<Tuple3<String, String, Number>> getData() {
        return data;
    }

    public List<String> getAdaptedAxisLabels() {
        return Stream.concat(Stream.of(labelForX), adaptedAxisLabels.stream()).collect(Collectors.toList());
    }

    public List<Tuple2<String, List<Number>>> getAdaptedData() { // needs to handle data for some columns missing
        final Map<String, List<Number>> result = new LinkedHashMap<>();
        adaptedData.forEach((x, sub) -> adaptedAxisLabels.forEach(y -> {
            final Number actual = sub.getOrDefault(y, BigDecimal.ZERO);
            result.computeIfAbsent(x, __ -> new ArrayList<>(0)).add(actual);
        }));
        return result.entrySet().stream()
                .map(e -> Tuple.of(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public void add(final String x, final String y, final Number z) {
        data.add(Tuple.of(x, y, z));
        adaptedAxisLabels.add(y);
        adaptedData.computeIfAbsent(x, __ -> new LinkedHashMap<>(0)).put(y, z);
    }

}
