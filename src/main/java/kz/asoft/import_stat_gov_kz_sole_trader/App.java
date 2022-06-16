package kz.asoft.import_stat_gov_kz_sole_trader;

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
                int cutId = -1;
                String cutName = "";
                JSONArray ja = new JSONArray(jsonString.toString());
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

                if (!log.isExistsCut(cutId)) {
                    log.addCut(cutId, cutName);
                }

                String listSitCodes = "";
                final String sqlText = "SELECT string_agg(id, ',') as lst FROM stat_gov_kz.d_situational_codes WHERE is_updated = true";
                try(Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery(sqlText);) {
                    while (resultSet.next()) {
                        listSitCodes = resultSet.getString("lst");
                    }
                }

                int pid;
                String[] files;
                FilenameFilter filter = (f, name) -> name.endsWith(".xlsx");
                final String sqlText2 = "SELECT id FROM stat_gov_kz.d_type_legal_unit WHERE is_updated = true";
                try(Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery(sqlText2);) {
                    while (resultSet.next()) {
                        pid = log.startProcess(cutId, resultSet.getInt("id"));
                        try {
                            // скачиваем файл
                            String fileName = new FileDownloader(proxy).getFile(cutId,
                                    resultSet.getInt("id"),
                                    listSitCodes,
                                    props.getProperty("downloadDir"));
                            // разархивируем файл
                            new UnzipUtility().unzip(props.getProperty("downloadDir") + "\\" + fileName,
                                    props.getProperty("downloadDir") + "\\" + fileName.split(".")[0]);
                            // загружаем данные с файла(ов)
                            files = new File(props.getProperty("downloadDir") + "\\" + fileName).list(filter);
                            for (String file : files) {
                                new ExcelDataLoader(conn).loadDataFile(file, cutId);
                            }
                            log.finishProcess(pid, null);
                        } catch (Exception e) {
                            log.finishProcess(pid, e.getMessage());
                            throw e;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
