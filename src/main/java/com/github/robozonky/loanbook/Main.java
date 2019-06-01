package com.github.robozonky.loanbook;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;
import freemarker.template.Configuration;

public class Main {

    private static Configuration getFreemarkerConfiguration(final Class<?> templateRoot) {
        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setClassForTemplateLoading(templateRoot, "");
        cfg.setLogTemplateExceptions(false);
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    private static void write(final Chart... charts) {
        try (var writer = Files.newBufferedWriter(Path.of("index.html"))) {
            final Map<String, Object> data = new LinkedHashMap<>();
            data.put("data", Map.of("charts", Arrays.asList(charts)));
            getFreemarkerConfiguration(Main.class)
                    .getTemplate("index.html.ftl")
                    .process(data, writer);
        } catch (final Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static void main(final String... args) throws IOException {
        final XLSXDownloader downloader = new XLSXDownloader();
        final InputStream s = downloader.get().orElseThrow(() -> new IllegalStateException("No loanbook available."));
        final XLSXConverter c = new XLSXConverter();
        final String[][] result = c.apply(s);
        final Data data = Data.process(result);
        final SortedMap<Ratio, Long> processed = data.getAll()
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(DataRow::getInterestRate, Collectors.counting()),
                        TreeMap::new
                ));
        final XYChart chartData = new XYChart("Půjčky podle úrokové míry", "Úroková míra [% p.a.]", "Počet půjček");
        processed.forEach((ratio, count) -> chartData.add(ratio.toString(), count));
        write(chartData);
    }

}
