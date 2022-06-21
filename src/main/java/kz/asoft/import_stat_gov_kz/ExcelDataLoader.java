package kz.asoft.import_stat_gov_kz;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class ExcelDataLoader {
    private final Legal legal;
    private final int typeLegalUnitId;

    ExcelDataLoader(Legal legal, int typeLegalUnitId) {
        this.legal = legal;
        this.typeLegalUnitId = typeLegalUnitId;
    }

    void loadDataFile(String fileLocation) throws Exception {
        String[] aRow = new String[16];

        InputStream is = Files.newInputStream(new File(fileLocation).toPath());
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        for (Sheet sheet : workbook) {
            System.out.println(sheet.getSheetName());
            rowLoop:
            for (Row r : sheet) {
                if (r.getRowNum() == 100) {
                    break;
                }
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
                legal.saveData(typeLegalUnitId, aRow);
            }
        }
    }
}
