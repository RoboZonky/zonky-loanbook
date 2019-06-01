package com.github.robozonky.loanbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final class XLSXDownloader implements Supplier<Optional<InputStream>> {

    private static final Logger LOGGER = LogManager.getLogger(XLSXDownloader.class);

    private static URL assembleURL(final LocalDate date) {
        final String monthString = "0" + date.getMonthValue();
        final String month = monthString.length() > 2 ? monthString.substring(1) : monthString;
        final int year = date.getYear();
        final String url = "https://zonky.cz/page-assets/documents/loanbook" + month + '_' + year + ".xlsx";
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            throw new IllegalStateException("Wrong URL:" + url, e);
        }
    }

    @Override
    public Optional<InputStream> get() {
        for (int i = 0; i < 3; i++) {
            final LocalDate now = LocalDate.now();
            final LocalDate monthToCheck = now.minusMonths(i);
            final URL loanbookUrl = assembleURL(monthToCheck);
            try {
                LOGGER.info("Using loanbook for {}.", YearMonth.from(monthToCheck));
                return Optional.of(loanbookUrl.openStream());
            } catch (final IOException e) {
                LOGGER.debug("Failed opening loanbook for " + monthToCheck, e);
            }
        }
        LOGGER.error("No recent loanbook available for download.");
        return Optional.empty();
    }
}
