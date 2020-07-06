package jt.upwork.com.logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jamestravol
 */
public class ExcelProcessor {

    public synchronized static void update(File file, HashMap<String, List<String[]>> data) {



        try {
            updateInternal(file, data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private static void updateInternal(File file, HashMap<String, List<String[]>> data) throws IOException {

        FileInputStream inputStream = new FileInputStream(file);
        //        final Workbook workbook = WorkbookFactory.create(true);
        final Workbook workbook = WorkbookFactory.create(inputStream);

        for (Map.Entry<String, List<String[]>> entry : data.entrySet()) {

            System.out.println("Adding the new data to file: " + entry.getKey() + ":" + entry.getValue());

            Row headerRow;
            Sheet sheet;
            
            if (Boolean.parseBoolean(Config.INSTANCE.getProperty("app.single.sheet"))) {
                sheet = getOrCreateSheet(workbook, "All");
            } else {
                sheet = getOrCreateSheet(workbook, entry.getKey());
            }

            if (sheet.getLastRowNum() < 1) {
                headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
            } else {
                headerRow = sheet.getRow(0);
            }

            int maxColIndex = 0;

            for (String[] dataRow : entry.getValue()) {
                final Row row = sheet.createRow(sheet.getLastRowNum() + 1);

                for (int i = 0; i < dataRow.length; i++) {

                    if (i > maxColIndex) {
                        maxColIndex = i;
                    }

                    if (headerRow.getLastCellNum() <= i) {
                        final Cell cell = headerRow.createCell(i);
                        if (Boolean.parseBoolean(Config.INSTANCE.getProperty("app.append.date"))) {
                            if (i == 0) {
                                cell.setCellValue("Date");
                            } else if (i == 1) {
                                cell.setCellValue("Time");
                            } else {
                                cell.setCellValue("Data " + (i - 1));
                            }
                        } else {
                            cell.setCellValue("Data " + (i + 1));
                        }
                    }

                    String val = dataRow[i];
                    final Cell cell = row.createCell(i);
                    cell.setCellValue(val);
                }

            }

            for (int i = 0; i <= maxColIndex; i++) {
                sheet.autoSizeColumn(i);
            }

        }

        System.out.println("Updating file");

        try (final FileOutputStream os = new FileOutputStream(file)) {
            workbook.write(os);
        }
        workbook.close();

        inputStream.close();

    }

    private static Sheet getOrCreateSheet(Workbook workbook, String name) {

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            final Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().equalsIgnoreCase(name)) {
                return sheet;
            }
        }

        return workbook.createSheet(name);
    }

    public synchronized static void createEmpty(File selectedFile) {

        System.out.println("Creating file");

        try {
            final Workbook workbook = WorkbookFactory.create(!selectedFile.getName().toLowerCase().endsWith(".xls"));

            try (final FileOutputStream os = new FileOutputStream(selectedFile)) {
                workbook.write(os);
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
