package com.github.robozonky.loanbook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import com.github.robozonky.loanbook.charts.AbstractChart;
import com.github.robozonky.loanbook.charts.AbstractXYZChart;
import com.github.robozonky.loanbook.input.Data;
import freemarker.template.Configuration;

final class Template implements Runnable {

    private final List<AbstractChart> charts = new CopyOnWriteArrayList<>();
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

    void addXYZChart(final Function<Data, AbstractXYZChart> constructor) {
        final AbstractXYZChart chart = constructor.apply(data);
        charts.add(chart);
    }

    private void process(final String filename) {
        try (final var writer = Files.newBufferedWriter(Path.of(filename))) {
            final Map<String, Object> data = new LinkedHashMap<>();
            data.put("data", Map.of("charts", charts,
                    "now", Date.from(Instant.now()),
                    "period", this.data.getYearMonth()));
            getFreemarkerConfiguration(Main.class)
                    .getTemplate(filename + ".ftl")
                    .process(data, writer);
        } catch (final Exception ex) {
            throw new IllegalStateException("Failed processing " + filename, ex);
        }
    }

    @Override
    public void run() {
        process("index.html");
        process("charts.js");
    }
}
