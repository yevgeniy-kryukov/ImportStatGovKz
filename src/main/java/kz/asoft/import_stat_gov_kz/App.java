package kz.asoft.import_stat_gov_kz;

import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FilenameFilter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;
import java.util.logging.*;

/**
 * ImportStatGovKz Application
 *
 */
public class App
{
    public static void main(String[] args)  {
        try (final Connection connDB = ConnDB.getConnection()) {

            LogDB logDB = null;
            try {
                PropertyConfigurator.configure("log4j.properties");
                // Считываем настройки приложения
                final Properties props = new Properties();
                try (InputStream in = Files.newInputStream(Paths.get("app.properties"))) {
                    props.load(in);
                }
                // Включаем логирование
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                FileHandler fileHandler = new FileHandler(props.getProperty("logPath")
                        + "/" + simpleDateFormat.format(new Timestamp(System.currentTimeMillis())) + ".log",
                        2000000, 5);
                fileHandler.setFormatter(new SimpleFormatter());
                Logger logger = Logger.getLogger(App.class.getName());
                logger.addHandler(fileHandler);
                // Если необходимо используем прокси
                Proxy proxy = null;
                if (props.getProperty("useProxy").equals("true")) {
                    proxy = new Proxy(Proxy.Type.HTTP,
                                        new InetSocketAddress(props.getProperty("proxyHost"),
                                        Integer.parseInt(props.getProperty("proxyPort"))));
                }
                // Получаем актульный идентификатор среза данных
                final Integer cutId = Cut.getCutId(connDB, proxy);
                // Стартуем журналирование
                logDB = new LogDB(connDB, cutId);
                if (!logDB.start()) {
                    return;
                }
                // Получаем строку ситуационных кодов разделенных ","
                final String sitCodes = SitCode.getAllCodes(connDB);
                final int katoId = 741880; // Казахстан
                int typeLegalUnitId;
                int okedId;
                String[] files;
                final FilenameFilter filter = (f, name) -> name.endsWith(".xlsx");
                try (final Statement statement = connDB.createStatement();
                     final ResultSet resultSet = statement.executeQuery("SELECT id FROM stat_gov_kz.d_type_legal_unit WHERE is_updated = true")) {
                    while (resultSet.next()) {
                        typeLegalUnitId = resultSet.getInt("id");
                        // скачиваем файл
                        try (final Statement statementOKED = connDB.createStatement();
                            final ResultSet resultSetOKED = statementOKED.executeQuery("SELECT item_id FROM stat_gov_kz.oked_list")) {
                            while (resultSetOKED.next()) {
                                okedId = resultSetOKED.getInt("item_id");
                                logger.log(Level.INFO, "loading data with okedID = " + okedId + ", typeLegalUnitID = " + typeLegalUnitId);
                                String fileName = new FileDownloader(proxy).getFile(cutId, typeLegalUnitId, sitCodes, okedId, katoId, props.getProperty("downloadDir"));
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
                                        new ExcelDataLoader(typeLegalUnitId, cutId, logger).loadDataFile(unzipPath + "\\" + file,
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
            System.out.println(e2.getMessage());
        }
    }

}
