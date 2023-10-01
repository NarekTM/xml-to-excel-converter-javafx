package com.narektm.xmltoexcelh;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelToExcelDataTransformer {
    private static final String DRAFT_INCOME_GROUP_IMPORT_EXCEL_FILE_PATH = "/files/draft-IncomeGroupImport.xlsm";

    public void transform(String sourceExcelFilePath) {
        try (
                FileInputStream sourceExcelFileInputStream = new FileInputStream(sourceExcelFilePath);
                Workbook sourceWorkbook = new XSSFWorkbook(sourceExcelFileInputStream);
                InputStream draftExcelFile = getClass().getResourceAsStream(DRAFT_INCOME_GROUP_IMPORT_EXCEL_FILE_PATH)) {
            assert draftExcelFile != null;
            try (Workbook targetWorkbook = new XSSFWorkbook(draftExcelFile)) {
                Sheet sourceSheet = sourceWorkbook.getSheetAt(0);
                Sheet targetSheet = targetWorkbook.getSheetAt(0);

                int targetRowIndex = 2; // Target starts from row number 3

                // Iterate over the source rows starting from row number 2
                for (int rowIndex = 1; rowIndex <= sourceSheet.getLastRowNum(); rowIndex++) {
                    Row sourceSheetRow = sourceSheet.getRow(rowIndex);
                    if (sourceSheetRow == null) continue;

                    Row targetSheetRow = targetSheet.createRow(targetRowIndex++);

                    int targetCellIndex = 0;
                    // Copy each cell except the column Q (which is index 16, 0-based)
                    for (int cellIndex = 0; cellIndex < sourceSheetRow.getLastCellNum(); cellIndex++) {
                        // Skip column Q
                        if (cellIndex == 16) {
                            continue;
                        }

                        Cell sourceCell = sourceSheetRow.getCell(cellIndex);
                        if (sourceCell == null) {
                            continue;
                        }

                        Cell targetCell = targetSheetRow.createCell(targetCellIndex++);
                        switch (sourceCell.getCellType()) {
                            case STRING:
                                targetCell.setCellValue(sourceCell.getStringCellValue());
                                break;
                            case NUMERIC:
                                targetCell.setCellValue(sourceCell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                targetCell.setCellFormula(sourceCell.getCellFormula());
                                break;
                            default:
                                break;
                        }
                    }
                }

                String targetPath = sourceExcelFilePath.replace(".xlsx", " IncomeGroupImport.xlsm");

                // Save the changes to the target file
                try (FileOutputStream fileOutputStream = new FileOutputStream(targetPath)) {
                    targetWorkbook.write(fileOutputStream);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
