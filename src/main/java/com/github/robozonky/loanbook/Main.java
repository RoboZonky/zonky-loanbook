package com.github.robozonky.loanbook;

import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import com.github.robozonky.loanbook.input.Ratio;

public class Main {

    public static void main(final String... args) {
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
        final Template template = new Template();
        template.addPieChart("Půjčky podle úrokové míry", "Úroková míra [% p.a.]", "Počet půjček", processed);
        template.run();
    }

}
