package com.github.robozonky.loanbook;

import java.io.InputStream;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
import io.vavr.Tuple;

public class Main {

    public static void main(final String... args) {
        final XLSXDownloader downloader = new XLSXDownloader();
        final InputStream s = downloader.get().orElseThrow(() -> new IllegalStateException("No loanbook available."));
        final XLSXConverter c = new XLSXConverter();
        final String[][] result = c.apply(s);
        final Template template = new Template(Data.process(result));
        template.addPieChart("Půjčky podle úrokové míry", "Úroková míra [% p.a.]", "Počet půjček",
                             (data, adder) -> data.getAll()
                                     .collect(Collectors.collectingAndThen(
                                             Collectors.groupingBy(DataRow::getInterestRate, Collectors.counting()),
                                             TreeMap::new
                                     ))
                                     .forEach((key, value) -> adder.accept(Tuple.of(key.toString(), value))));
        template.run();
    }
}
