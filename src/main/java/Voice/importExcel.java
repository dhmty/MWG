/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Voice;

import MIP.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
        
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author dhmty
 */
public class importExcel {

     public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;

    public static void main(String args[]) throws IOException {
        final String excelFilePath = "data_Import/book.xlsx";
        final List<model> list =readExcel(excelFilePath);

        System.out.println(list.size());
        for (model sh : list) {
            System.out.println(sh.toString());
            System.out.println(sh.getId()+" "+sh.getName());
        }
    }
    public static List<model> readExcel(String excelFilePath) throws IOException {
        List<model> list = new ArrayList<>();
        
        int sl=0;
        // Get file
        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
 
        // Get workbook
        Workbook workbook = getWorkbook(inputStream, excelFilePath);
 
        // Get sheet
        Sheet sheet = workbook.getSheetAt(0);
 
        // Get all rows
        Iterator<Row> iterator = sheet.iterator();
   
        // kiểm tra để thêm vào list
        
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            if (nextRow.getRowNum() == 0) {
                // Ignore header
                continue;
                
            }

            model prod = new model();
          
                // Get all cells
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            while (cellIterator.hasNext()) {
                //Read cell
                  Cell cell = cellIterator.next();
                  Object cellValue = getCellValue(cell);
                  if (cellValue == null || cellValue.toString().isEmpty()) {
                          continue;
                  }
                  // Set value for book object
                  int columnIndex = cell.getColumnIndex();
                  switch (columnIndex) {
                    case COLUMN_INDEX_ID:
                    {
                               prod.setId(String.valueOf(getCellValue(cell)));
                    }
                    case COLUMN_INDEX_NAME: {
                               prod.setName(String.valueOf(getCellValue(cell)));
                    }

                    default:{}
                  }
            }
                  list.add(prod);
                sl++;
        }
        workbook.close();
        inputStream.close();
        System.out.println(sl);
        return list;
    }
 
    // Get Workbook
    private static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
        Workbook workbook = null;
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }
 
        return workbook;
    }
 
    // Get cell value
    private static Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellTypeEnum();
        Object cellValue = null;
        switch (cellType) {
        case BOOLEAN:
            cellValue = cell.getBooleanCellValue();
            break;
        case FORMULA:
            Workbook workbook = cell.getSheet().getWorkbook();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            cellValue = evaluator.evaluate(cell).getNumberValue();
            break;
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
			cellValue = cell.getDateCellValue();
            } else {
			cellValue = cell.getNumericCellValue();
                    }
            break;
        case STRING:
            cellValue = cell.getStringCellValue();
            break;
        case _NONE:
        case BLANK:
        case ERROR:
            break;
        default:
            break;
        }
 
        return cellValue;
    }
}

