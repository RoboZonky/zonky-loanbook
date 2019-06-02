package com.github.robozonky.loanbook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.robozonky.loanbook.charts.Chart;
import com.github.robozonky.loanbook.charts.ChartType;
import com.github.robozonky.loanbook.charts.XYChart;
import com.github.robozonky.loanbook.charts.XYZChart;
import com.github.robozonky.loanbook.input.Data;
import freemarker.template.Configuration;
import io.vavr.Tuple2;
import io.vavr.Tuple3;

final class Template implements Runnable {

    private final List<Chart> charts = new CopyOnWriteArrayList<>();
    private final Data data;

    public Template(final Data data) {
        this.data = data;
    }

    private static Configuration getFreemarkerConfiguration(final Class<?> templateRoot) {
        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setClassForTemplateLoading(templateRoot, "");
        cfg.setLogTemplateExceptions(false);
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    private String addDateToTitle(final String title) {
        return title + " (Zonky do " + data.getYearMonth() + " vč.)";
    }

    void addPieChart(final String title, final String labelForX, final String labelForY,
                     final BiConsumer<Data, Consumer<Tuple2<String, Number>>> processor) {
        final XYChart chart = new XYChart(ChartType.PIE, addDateToTitle(title), labelForX, labelForY);
        charts.add(chart);
        processor.accept(data, tuple -> chart.add(tuple._1, tuple._2));
    }

    void addBarChart(final String title, final String labelForX, final String labelForY, final String labelForZ,
                     final BiConsumer<Data, Consumer<Tuple3<String, String, Number>>> processor) {
        final XYZChart chart = new XYZChart(ChartType.BAR, addDateToTitle(title), labelForX, labelForY, labelForZ);
        charts.add(chart);
        processor.accept(data, tuple -> chart.add(tuple._1, tuple._2, tuple._3));
    }

    void addColumnChart(final String title, final String labelForX, final String labelForY, final String labelForZ,
                     final BiConsumer<Data, Consumer<Tuple3<String, String, Number>>> processor) {
        final XYZChart chart = new XYZChart(ChartType.COLUMN, addDateToTitle(title), labelForX, labelForY, labelForZ);
        charts.add(chart);
        processor.accept(data, tuple -> chart.add(tuple._1, tuple._2, tuple._3));
    }



    @Override
    public void run() {
        try (final var writer = Files.newBufferedWriter(Path.of("index.html"))) {
            final Map<String, Object> data = new LinkedHashMap<>();
            data.put("data", Map.of("charts", charts, "now", Date.from(Instant.now())));
            getFreemarkerConfiguration(Main.class)
                    .getTemplate("index.html.ftl")
                    .process(data, writer);
        } catch (final Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
