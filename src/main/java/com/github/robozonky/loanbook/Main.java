package com.github.robozonky.loanbook;

import java.io.InputStream;

import com.github.robozonky.loanbook.model.Data;

public class Main {

    public static void main(final String... args) {
        final XLSXDownloader downloader = new XLSXDownloader();
        final InputStream s = downloader.get().orElseThrow(() -> new IllegalStateException("No loanbook available."));
        final XLSXConverter c = new XLSXConverter();
        final String[][] result = c.apply(s);
        System.out.println(Data.process(result));
    }

}
