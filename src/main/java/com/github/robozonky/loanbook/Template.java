package com.github.robozonky.loanbook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.github.robozonky.loanbook.charts.Chart;
import com.github.robozonky.loanbook.charts.ChartType;
import com.github.robozonky.loanbook.charts.XYChart;
import freemarker.template.Configuration;

final class Template implements Runnable {

    private final List<Chart> charts = new ArrayList<>(0);

    public void addPieChart(final String title, final String labelForX, final String labelForY,
                            final SortedMap<? extends Comparable<?>, ? extends Number> data) {
        final XYChart chart = new XYChart(ChartType.PIE, title, labelForX, labelForY);
        data.forEach((key, value) -> chart.add(key.toString(), value));
        charts.add(chart);
    }

    private static Configuration getFreemarkerConfiguration(final Class<?> templateRoot) {
        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setClassForTemplateLoading(templateRoot, "");
        cfg.setLogTemplateExceptions(false);
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    @Override
    public void run() {
        try (final var writer = Files.newBufferedWriter(Path.of("index.html"))) {
            final Map<String, Object> data = new LinkedHashMap<>();
            data.put("data", Map.of("charts", charts));
            getFreemarkerConfiguration(Main.class)
                    .getTemplate("index.html.ftl")
                    .process(data, writer);
        } catch (final Exception ex) {
            throw new IllegalStateException(ex);
        }

    }
}
