package com.github.robozonky.loanbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.robozonky.loanbook.charts.IncomeTypeRiskChart;
import com.github.robozonky.loanbook.charts.InsuranceRiskChart;
import com.github.robozonky.loanbook.charts.InterestRateDefaultRatesTimelineChart;
import com.github.robozonky.loanbook.charts.InterestRateDefaultTimelineChart;
import com.github.robozonky.loanbook.charts.InvestorRiskChart;
import com.github.robozonky.loanbook.charts.PreviousLoansRiskChart;
import com.github.robozonky.loanbook.charts.PrincipalRiskChart;
import com.github.robozonky.loanbook.charts.PurposeRiskChart;
import com.github.robozonky.loanbook.charts.RegionRiskChart;
import com.github.robozonky.loanbook.charts.StoryAndInsuranceTimelineChart;
import com.github.robozonky.loanbook.charts.StoryRiskChart;
import com.github.robozonky.loanbook.charts.SummaryIncomeTypeTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryInterestRateTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryPurposeTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryRegionTimelineChart;
import com.github.robozonky.loanbook.charts.TermRiskChart;
import com.github.robozonky.loanbook.charts.VintageDefaultRatesTimelineChart;
import com.github.robozonky.loanbook.input.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static void saveJs(final String filename) {
        try (final InputStream s = Main.class.getResourceAsStream(filename)) {
            final byte[] bytes = s.readAllBytes();
            Files.write(Path.of(filename), bytes);
            LOGGER.info("Saved {}.", filename);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(final String... args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expecting one argument, URL of the loanbook.");
        }
        final URL url = new URL(args[0]);
        try (final InputStream s = url.openStream()) {
            final XLSXConverter c = new XLSXConverter();
            final String[][] result = c.apply(s);
            final Template template = new Template(Data.process(result));
            // bar charts
            template.addXYZChart(PurposeRiskChart::new);
            template.addXYZChart(RegionRiskChart::new);
            template.addXYZChart(IncomeTypeRiskChart::new);
            template.addXYZChart(PrincipalRiskChart::new);
            template.addXYZChart(TermRiskChart::new);
            template.addXYZChart(StoryRiskChart::new);
            template.addXYZChart(InsuranceRiskChart::new);
            template.addXYZChart(InvestorRiskChart::new);
            template.addXYZChart(PreviousLoansRiskChart::new);
            // line charts
            template.addXYZChart(InterestRateDefaultTimelineChart::new);
            template.addXYZChart(InterestRateDefaultRatesTimelineChart::new);
            template.addXYZChart(VintageDefaultRatesTimelineChart::new);
            template.addXYZChart(StoryAndInsuranceTimelineChart::new);
            template.addXYZChart(SummaryInterestRateTimelineChart::new);
            template.addXYZChart(SummaryPurposeTimelineChart::new);
            template.addXYZChart(SummaryRegionTimelineChart::new);
            template.addXYZChart(SummaryIncomeTypeTimelineChart::new);
            template.run();
            saveJs("canvg.js");
            saveJs("rgbcolor.js");
            saveJs("svgprint.js");
        }
    }
}
