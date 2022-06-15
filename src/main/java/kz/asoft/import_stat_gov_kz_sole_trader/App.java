package kz.asoft.import_stat_gov_kz_sole_trader;

import org.json.JSONArray;

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
        try {
            // считываем настройки
            final Properties props = new Properties();
            try(InputStream in = Files.newInputStream(Paths.get("app.properties"))) {
                props.load(in);
            }

            try (final Connection conn = ConnDB.getConnection()) {

                Log log = new Log(conn);
                if (log.isExistsUnfinishedProcess()) {
                    return;
                }

                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.1.20.15", 8888));

                // Получение списка срезов
                String jsonString = new HttpUtility().get("https://stat.gov.kz/api/rcut/ru", proxy);
                if (jsonString == null) {
                    throw new Exception("Ошибка! Не удалось получить список срезов");
                }

                // Находим идентификатор последнего среза
                int lastCutId = -1;
                String lastCutName = "";
                JSONArray ja = new JSONArray(jsonString.toString());
                for (int i = 0; i < ja.length(); i++) {
                    int id = ja.getJSONObject(i).getInt("id");
                    if (id > lastCutId) {
                        lastCutId = id;
                        lastCutName = ja.getJSONObject(i).getString("name");
                    }
                }

                if (lastCutId == -1) {
                    return;
                }

                if (!log.isExistsCut(lastCutId)) {
                    log.addCut(lastCutId, lastCutName);
                }

                final String sqlText = "SELECT id FROM stat_gov_kz.d_type_legal_unit WHERE is_updated = true";
                try(Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery(sqlText);) {
                    while (resultSet.next()) {
                        log.startProcess(lastCutId, resultSet.getInt("id"));

                        //            // скачиваем файл
                        //            FileDownloader fd = new FileDownloader(proxy);
                        //            String fileName = fd.getFile(lastCutId, 742681, props.getProperty("downloadDir"));
                        //            // разархивируем файл
                        //            new UnzipUtility().unzip(props.getProperty("downloadDir") + "\\" + fileName, props.getProperty("downloadDir"));

                        new ExcelDataLoader().loadDataFile("C:\\Windows\\Temp\\request-4286c2b12e6bbfdc4bc079caef7fc2ce.xlsx");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
