package kz.asoft.import_stat_gov_kz_sole_trader;

import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        try {
//            // считываем настройки
//            final Properties props = new Properties();
//            try(InputStream in = Files.newInputStream(Paths.get("app.properties"))) {
//                props.load(in);
//            }
//            // скачиваем файл
//            FileDownloader fd = new FileDownloader();
//            String fileName = fd.getFile(742681, props.getProperty("downloadDir"));
//            // разархивируем файл
//            new UnzipUtility().unzip(props.getProperty("downloadDir") + "\\" + fileName, props.getProperty("downloadDir"));

            new ExcelDataLoader().loadDataFile("C:\\Windows\\Temp\\request-4286c2b12e6bbfdc4bc079caef7fc2ce.xlsx");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
