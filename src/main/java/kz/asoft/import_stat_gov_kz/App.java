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

        try (Connection conn = ConnDB.getConnection()) {

            org.apache.log4j.PropertyConfigurator.configure("log4j.properties");

            // Считываем настройки
            final Properties props = new Properties();
            try (InputStream in = Files.newInputStream(Paths.get("app.properties"))) {
                props.load(in);
            }

            Proxy proxy = null;
            if (props.getProperty("useProxy").equals("true")) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(props.getProperty("proxyHost"), Integer.parseInt(props.getProperty("proxyPort"))));
            }

            // Получение списка срезов
            String jsonString = new HttpUtility(proxy).get("https://stat.gov.kz/api/rcut/ru");
            if (jsonString == null) {
                throw new Exception("Ошибка! Не удалось получить список срезов");
            }

            // Находим идентификатор последнего среза
            int cutId = -1;
            String cutName = "";
            JSONArray ja = new JSONArray(jsonString);
            for (int i = 0; i < ja.length(); i++) {
                int id = ja.getJSONObject(i).getInt("id");
                if (id > cutId) {
                    cutId = id;
                    cutName = ja.getJSONObject(i).getString("name");
                }
            }

            if (cutId == -1) {
                return;
            }

            Cut cut = new Cut(conn);
            if (!cut.isExistsCut(cutId)) {
                cut.addCut(cutId, cutName);
            }

            log = new Log(conn, cutId);

            if (!log.start()) {
                return;
            }

            String listSitCodes = "";
            final String sqlText = "SELECT string_agg(id, ',') as lst FROM stat_gov_kz.d_situational_codes WHERE is_updated = true";
            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlText)) {
                while (resultSet.next()) {
                    listSitCodes = resultSet.getString("lst");
                }
            }

            int typeLegalUnitId;
            String[] files;
            FilenameFilter filter = (f, name) -> name.endsWith(".xlsx");
            final String sqlText2 = "SELECT id FROM stat_gov_kz.d_type_legal_unit WHERE is_updated = true";
            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlText2)) {
                while (resultSet.next()) {
                    typeLegalUnitId = resultSet.getInt("id");
                    // скачиваем файл
                    String fileName = new FileDownloader(proxy).getFile(cutId, typeLegalUnitId, listSitCodes, props.getProperty("downloadDir"));
                    String unzipPath = props.getProperty("downloadDir") + "\\" + fileName.split("\\.")[0];
                    // разархивируем файл
                    new UnzipUtility().unzip(props.getProperty("downloadDir") + "\\" + fileName, unzipPath);
                    // загружаем данные с файла(ов)
                    files = new File(unzipPath).list(filter);
                    for (String file : files) {
                        new ExcelDataLoader(conn, cutId, typeLegalUnitId).loadDataFile(unzipPath + "\\" + file);
                    }
                }
            }

            log.finish(null);

        } catch (Exception e) {
            try {
                if (log != null) log.finish(e.getMessage());
            } catch (Exception eLog) {
                eLog.printStackTrace();
            }
        }
    }

}
