package com.github.robozonky.loanbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

final class XLSXConverter implements Consumer<InputStream> {

    private static final Logger LOGGER = LogManager.getLogger(XLSXConverter.class);
    private final DataFormatter objDefaultFormat = new DataFormatter();
    private final Consumer<String[]> rowProcessor;

    public XLSXConverter(final Consumer<String[]> rowProcessor) {
        this.rowProcessor = rowProcessor;
    }

    private String[] processRow(Row row) {
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
        try (final XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            LOGGER.debug("Workbook loaded.");
            var sheet = workbook.getSheet("data");
            var numRows = sheet.getPhysicalNumberOfRows();
            for (int rowId = 1; rowId < numRows; rowId++) { // row 0 is just headers
                String[] row = processRow(sheet.getRow(rowId));
                rowProcessor.accept(row);
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
