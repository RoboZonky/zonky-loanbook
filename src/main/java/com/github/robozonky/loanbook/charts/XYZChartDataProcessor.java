package com.github.robozonky.loanbook.charts;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.github.robozonky.loanbook.input.DataRow;

interface XYZChartDataProcessor extends BiConsumer<Stream<DataRow>, XYZChartDataConsumer> {

}
