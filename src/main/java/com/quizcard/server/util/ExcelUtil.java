package com.quizcard.server.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.text.DecimalFormat;

public class ExcelUtil {

    public static String getValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType().equals(CellType.NUMERIC)) {
            DecimalFormat format = new DecimalFormat();

            format.setDecimalSeparatorAlwaysShown(false);
            return format.format(cell.getNumericCellValue());
        }
        if (cell.getCellType().equals(CellType.STRING)) {
            return cell.getStringCellValue();
        }
        return cell.toString();
    }

    public static Integer getNumberValue(Cell cell) {
        if (cell.getCellType().equals(CellType.NUMERIC)) {
            return (int) cell.getNumericCellValue();
        }
        return null;
    }
}
