package com.github.robozonky.loanbook.charts;

import java.util.function.Consumer;

import io.vavr.Tuple3;

interface XYZChartDataConsumer extends Consumer<Tuple3<String, Comparable, Number>> {

}
