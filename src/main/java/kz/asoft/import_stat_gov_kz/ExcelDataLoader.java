package kz.asoft.import_stat_gov_kz;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

class ExcelDataLoader {
    private final int typeLegalUnitId;

    private final int cutId;

    ExcelDataLoader(int typeLegalUnitId, int cutId) {
        this.cutId = cutId;
        this.typeLegalUnitId = typeLegalUnitId;
    }

    void loadDataFile(String fileLocation, int countLoadThreads) throws Exception {
        int sheetIndex = 0;
        ArrayList<Thread> listThreads = new ArrayList<Thread>();

        InputStream is = Files.newInputStream(new File(fileLocation).toPath());
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        for (int i = 1; i <= Math.ceil((double) workbook.getNumberOfSheets() / countLoadThreads); i++) {
            for (int j = 1; j <= countLoadThreads; j++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Thread t = new SheetThread("SheetThread" + sheetIndex, sheet);
                t.start();
                listThreads.add(t);
                sheetIndex = sheetIndex + 1;
                if (sheetIndex == workbook.getNumberOfSheets()) {
                    break;
                }
            }
            for(Thread item : listThreads) {
                try {
                    item.join();
                } catch(InterruptedException e){
                    System.out.printf("%s has been interrupted", item.getName());
                }
            }
        }
    }

    class SheetThread extends Thread {
        final private Sheet sheet;

        SheetThread(String threadName, Sheet sheet) {
            super(threadName);
            this.sheet = sheet;
        }

        public void run() {
            try {
                System.out.println(sheet.getSheetName());

                Legal legal = new Legal(cutId, typeLegalUnitId);

                String[] aRow = new String[16];
                rowLoop:
                for (Row r : this.sheet) {
//                if (r.getRowNum() == 100) {
//                    break;
//                }
                    for (Cell c : r) {
                        if (c.getColumnIndex() == 0) { // в первой колонке должен быть ИИН (БИН)
                            try {
                                long cellValue = Long.parseLong(c.getStringCellValue().trim());
                            } catch (NumberFormatException nfe) {
                                continue rowLoop;
                            }
                        }
                        aRow[c.getColumnIndex()] = c.getStringCellValue().trim();
                    }

                    legal.saveRow(aRow);
                }

                legal.close();

            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }
}
