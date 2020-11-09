package com.github.robozonky.loanbook;

import java.io.InputStream;
import java.util.function.Consumer;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

final class XLSXConverter implements Consumer<InputStream> {

    private static final Logger LOGGER = LogManager.getLogger(XLSXConverter.class);
    private final DataFormatter objDefaultFormat = new DataFormatter();
    private final Consumer<String[]> rowProcessor;

    public XLSXConverter(final Consumer<String[]> rowProcessor) {
        this.rowProcessor = rowProcessor;
    }

    private String[] adaptRow(Row row) {
        var lastCellNum = row.getLastCellNum();
        var rowCells = new String[lastCellNum + 1];
        for (int colId = 0; colId <= lastCellNum; colId++) {
            var cellValue = objDefaultFormat.formatCellValue(row.getCell(colId));
            rowCells[colId] = cellValue;
        }
        LOGGER.trace("Parsed {}.", (Object) rowCells);
        return rowCells;
    }

    @Override
    public void accept(final InputStream inputStream) {
        var workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(inputStream);
        LOGGER.debug("Workbook loaded.");
        var sheet = workbook.getSheet("data");
        boolean isFirstRow = true;
        for (Row r : sheet) {
            if (isFirstRow) { // Skip first row, which is just headers.
                isFirstRow = false;
                continue;
            }
            rowProcessor.accept(adaptRow(r));
        }
    }
}
