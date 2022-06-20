package kz.asoft.import_stat_gov_kz;

import org.json.JSONArray;

import java.io.File;
import java.io.FilenameFilter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
        Log log = null;

        try (Connection connDB = ConnDB.getConnection()) {

            org.apache.log4j.PropertyConfigurator.configure("log4j.properties");

            // Считываем настройки
            final Properties props = new Properties();
            try (InputStream in = Files.newInputStream(Paths.get("app.properties"))) {
                props.load(in);
            }

            Proxy proxy = null;
            if (props.getProperty("useProxy").equals("true")) {
                proxy = new Proxy(Proxy.Type.HTTP,
                                new InetSocketAddress(props.getProperty("proxyHost"),
                                Integer.parseInt(props.getProperty("proxyPort"))));
            }

            Cut cut = new Cut(connDB, proxy);

            log = new Log(connDB, cut.getCutId());

            if (!log.start()) {
                return;
            }

            String sitCodes = new SitCode(connDB).getAllCodes();

            int typeLegalUnitId;
            String[] files;
            FilenameFilter filter = (f, name) -> name.endsWith(".xlsx");
            final String sqlText2 = "SELECT id FROM stat_gov_kz.d_type_legal_unit WHERE is_updated = true";
            try (Statement statement = connDB.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlText2)) {
                while (resultSet.next()) {
                    typeLegalUnitId = resultSet.getInt("id");
                    // скачиваем файл
                    String fileName = new FileDownloader(proxy).getFile(cut.getCutId(),
                                                                        typeLegalUnitId,
                                                                        sitCodes,
                                                                        props.getProperty("downloadDir"));
                    // разархивируем файл
                    String unzipPath = props.getProperty("downloadDir") + "\\" + fileName.split("\\.")[0];
                    new UnzipUtility().unzip(props.getProperty("downloadDir") + "\\" + fileName, unzipPath);
                    // загружаем данные с файла(ов)
                    files = new File(unzipPath).list(filter);
                    for (String file : files) {
                        new ExcelDataLoader(connDB, cut.getCutId(), typeLegalUnitId).loadDataFile(unzipPath + "\\" + file);
                    }
                }
            }

            log.finish(null);

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (log != null) log.finish(e.getMessage());
            } catch (Exception eLog) {
                eLog.printStackTrace();
            }
        }
    }

}
