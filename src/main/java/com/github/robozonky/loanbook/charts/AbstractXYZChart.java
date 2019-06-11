package com.github.robozonky.loanbook.charts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;

public abstract class AbstractXYZChart extends AbstractChart {

    private final Set<String> adaptedAxisLabels = new LinkedHashSet<>();
    private final Map<String, Map<String, Number>> adaptedData = new LinkedHashMap<>();
    private final List<Tuple3<String, String, Number>> data = new ArrayList<>(0);

    protected AbstractXYZChart(final Data data, final XYZChartDataProcessor processor) {
        super(data);
        processor.accept(getApplicableDataRows(), tuple -> add(tuple._1, tuple._2, tuple._3));
    }

    public abstract boolean isRatingsAsSeries();

    public abstract String getLabelForX();

    public abstract String getLabelForY();

    public abstract String getLabelForZ();

    public List<Tuple3<String, String, Number>> getData() {
        return data;
    }

    public List<String> getAdaptedAxisLabels() {
        return Stream.concat(Stream.of(getLabelForX()), adaptedAxisLabels.stream()).collect(Collectors.toList());
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

    private void add(final String x, final String y, final Number z) {
        data.add(Tuple.of(x, y, z));
        adaptedAxisLabels.add(y);
        adaptedData.computeIfAbsent(x, __ -> new LinkedHashMap<>(0)).put(y, z);
    }

    @Override
    public int getAxisCount() {
        return 3;
    }
}
