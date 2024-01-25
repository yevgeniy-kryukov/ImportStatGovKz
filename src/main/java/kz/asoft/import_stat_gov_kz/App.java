package kz.asoft.import_stat_gov_kz;

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
 * ImportStatGovKz Application
 *
 */
public class App
{
    public static void main(String[] args)  {
        try (Connection connDB = ConnDB.getConnection()) {
            LogDB logDB = null;
            try {
                org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
                // Считываем настройки приложения
                final Properties props = new Properties();
                try (InputStream in = Files.newInputStream(Paths.get("app.properties"))) {
                    props.load(in);
                }
                // Если необходимо используем прокси
                Proxy proxy = null;
                if (props.getProperty("useProxy").equals("true")) {
                    proxy = new Proxy(Proxy.Type.HTTP,
                                        new InetSocketAddress(props.getProperty("proxyHost"),
                                        Integer.parseInt(props.getProperty("proxyPort"))));
                }
                // Получаем актульный идентификатор среза данных
                Integer cutId = Cut.getCutId(connDB, proxy);
                // Стартуем журналирование
                logDB = new LogDB(connDB, cutId);
                if (!logDB.start()) {
                    return;
                }
                // Получаем строку ситуационных кодов разделенных ","
                final String sitCodes = SitCode.getAllCodes(connDB);
                final int katoId = 741880; // Казахстан
                int typeLegalUnitId;
                String[] files;
                FilenameFilter filter = (f, name) -> name.endsWith(".xlsx");
                try (Statement statement = connDB.createStatement();
                     ResultSet resultSet = statement.executeQuery("SELECT id FROM stat_gov_kz.d_type_legal_unit WHERE is_updated = true")) {
                    while (resultSet.next()) {
                        typeLegalUnitId = resultSet.getInt("id");
                        // скачиваем файл
                        try (Statement statementOKED = connDB.createStatement();
                            ResultSet resultSetOKED = statementOKED.executeQuery("SELECT item_id FROM stat_gov_kz.oked_list")) {
                            while (resultSetOKED.next()) {
                                String fileName = new FileDownloader(proxy).getFile(cutId,
                                        typeLegalUnitId,
                                        sitCodes,
                                        resultSetOKED.getInt("item_id"),
                                        katoId,
                                        props.getProperty("downloadDir"));
                                if (fileName == null) {
                                    continue;
                                }
                                // разархивируем файл
                                String unzipPath = props.getProperty("downloadDir") + "\\" + fileName.split("\\.")[0];
                                new UnzipUtility().unzip(props.getProperty("downloadDir") + "\\" + fileName, unzipPath);
                                // загружаем данные с файла(ов)
                                files = new File(unzipPath).list(filter);
                                if (files != null) {
                                    for (String file : files) {
                                        new ExcelDataLoader(typeLegalUnitId, cutId).loadDataFile(unzipPath + "\\" + file,
                                                Integer.parseInt(props.getProperty("countLoadThreads")));
                                    }
                                }
                            }
                        }
                    }
                }
                // Установка признака о неактульности
                Legal.setNotActual(connDB, cutId);
                // Завершаем журналирование
                logDB.finish(null);
            } catch (Exception e) {
                if (logDB != null) logDB.finish(e.getMessage());
                throw e;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

}
