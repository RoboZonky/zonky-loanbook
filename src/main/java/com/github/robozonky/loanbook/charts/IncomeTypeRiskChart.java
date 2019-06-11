package com.github.robozonky.loanbook.charts;

import java.util.Optional;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.Data;
import com.github.robozonky.loanbook.input.DataRow;

public final class IncomeTypeRiskChart extends AbstractRiskXYZChart {

    public IncomeTypeRiskChart(final Data data) {
        super(data, IncomeTypeRiskChart::incomeRiskChart);
    }

    private static void incomeRiskChart(final Stream<DataRow> data,
                                        final XYZChartDataConsumer adder) {
        abstractRiskChart(data, DataRow::getIncomeType, adder);
    }

    @Override
    public String getLabelForX() {
        return "Zdroj příjmu";
    }

    @Override
    public Stream<DataRow> getApplicableDataRows() {
        return super.getApplicableDataRows().filter(r -> !r.getIncomeType().equals("Nezaměstnaný"));
    }

    @Override
    public Optional<String> getComment() {
        return Optional.of("Nezahrnuje všech 7 půjček, kde žadatel byl 'Nezaměstnaný'" +
                                   " - tyto půjčky už Zonky neposkytuje a v kontextu jiných druhů příjmů " +
                                   "tak 15 % zesplatnění (1 ks) nežádoucím způsobem zkreslovalo grafy.");
    }

    @Override
    public String getTitle() {
        return "Zesplatněné půjčky podle účelu";
    }
}
