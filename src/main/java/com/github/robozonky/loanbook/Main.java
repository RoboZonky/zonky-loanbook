package com.github.robozonky.loanbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.github.robozonky.loanbook.charts.GlobalSummaryTimelineChart;
import com.github.robozonky.loanbook.charts.IncomeTypeRiskChart;
import com.github.robozonky.loanbook.charts.InsuranceRiskChart;
import com.github.robozonky.loanbook.charts.InterestRateDefaultRatesTimelineChart;
import com.github.robozonky.loanbook.charts.InterestRateDefaultTimelineChart;
import com.github.robozonky.loanbook.charts.InvestorRiskChart;
import com.github.robozonky.loanbook.charts.PreviousLoansRiskChart;
import com.github.robozonky.loanbook.charts.PrincipalRiskChart;
import com.github.robozonky.loanbook.charts.PurposeRiskChart;
import com.github.robozonky.loanbook.charts.RegionRiskChart;
import com.github.robozonky.loanbook.charts.StoryRiskChart;
import com.github.robozonky.loanbook.charts.SummaryIncomeTypeTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryInterestRateTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryPrincipalTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryPurposeTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryRegionTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryTermTimelineChart;
import com.github.robozonky.loanbook.charts.SummaryTimelineChart;
import com.github.robozonky.loanbook.charts.TermRiskChart;
import com.github.robozonky.loanbook.charts.VintageDefaultRatesTimelineChart;
import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;
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

    private static void process(final URL url, final Consumer<String[]> rowProcessor) throws IOException {
        try (final InputStream s = url.openStream()) {
            final XLSXConverter c = new XLSXConverter(rowProcessor);
            c.accept(s);
        }
    }

    public static void main(final String... args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expecting one argument, URL of the loanbook.");
        }
        final URL url = new URL(args[0]);
        final Set<DataRow> rows = new LinkedHashSet<>();
        process(url, row -> {
            final DataRow data = Data.process(row);
            synchronized (rows) {
                rows.add(data);
            }
        });
        LOGGER.info("Loaded {} data rows.", rows.size());
        final Template template = new Template(new Data(rows));
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
        template.addXYZChart(GlobalSummaryTimelineChart::new);
        template.addXYZChart(SummaryTimelineChart::new);
        template.addXYZChart(SummaryInterestRateTimelineChart::new);
        template.addXYZChart(SummaryPurposeTimelineChart::new);
        template.addXYZChart(SummaryRegionTimelineChart::new);
        template.addXYZChart(SummaryIncomeTypeTimelineChart::new);
        template.addXYZChart(SummaryPrincipalTimelineChart::new);
        template.addXYZChart(SummaryTermTimelineChart::new);
        template.run();
        saveJs("canvg.js");
        saveJs("rgbcolor.js");
        saveJs("svgprint.js");
    }
}
