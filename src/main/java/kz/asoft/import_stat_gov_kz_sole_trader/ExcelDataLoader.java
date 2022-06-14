package kz.asoft.import_stat_gov_kz_sole_trader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class ExcelDataLoader {
    public void loadDataFile(String fileLocation) throws Exception {
        org.apache.log4j.PropertyConfigurator.configure("log4j.properties");

        InputStream is = Files.newInputStream(new File(fileLocation).toPath());
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        int i = 0;
        for (Sheet sheet : workbook){
            i = 0;
            System.out.println(sheet.getSheetName());
            for (Row r : sheet) {
                i++;
                for (Cell c : r) {
                    System.out.println(c.getStringCellValue());
                }

                if (i==10) {
                   break;
                }

            }
        }
    }
}
