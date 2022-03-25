package com.ghco.positioncalculator.util;

import lombok.NonNull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExcelFileUtil {

    private ExcelFileUtil() throws IllegalAccessException {
        throw new IllegalAccessException("CsvUtil cannot be instantiated");
    }

    @Nullable
    public static String getXssfCellValue(@NonNull XSSFSheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null;
        }

        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell != null) {
            return getXssfCellValue(sheet, rowIndex, colIndex, cell.getCellType());
        }

        return null;
    }

    @Nullable
    public static String getXssfCellValue(@NonNull XSSFSheet sheet, int rowIndex, int colIndex, CellType cellType) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null;
        }

        String cellValue = null;
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cellType == CellType.BOOLEAN) {
            cellValue = String.valueOf(cell.getBooleanCellValue());
        } else if (cellType == CellType.FORMULA) {
            cellValue = cell.getCellFormula();
        } else if (cellType == CellType.NUMERIC) {
            cellValue = BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, RoundingMode.CEILING).toPlainString();
        } else if (cellType == CellType.STRING) {
            cellValue = cell.getStringCellValue().trim();
        }

        return cellValue;
    }

    @Nullable
    public static XSSFSheet getWorksheetFromExcelFile(@NonNull final File file) {
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } catch (Exception e) {
            return null;
        }
        return workbook.getSheetAt(0);
    }

}
