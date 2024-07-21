package kz.asoft.import_stat_gov_kz;

import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
 */
public class App {
    private static Properties getProperties() throws IOException {
        final Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("app.properties"))) {
            props.load(in);
        }
        return props;
    }

    private static Logger getLogger(final Properties props) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        FileHandler fileHandler = new FileHandler(props.getProperty("logPath")
                + "/" + simpleDateFormat.format(new Timestamp(System.currentTimeMillis())) + ".log",
                20000000, 1);
        fileHandler.setFormatter(new SimpleFormatter());
        Logger logger = Logger.getLogger(App.class.getName());
        logger.addHandler(fileHandler);
        return logger;
    }

    private static Proxy getProxy(final Properties props) {
        Proxy proxy = null;
        if (props.getProperty("useProxy").equals("true")) {
            proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(props.getProperty("proxyHost"),
                            Integer.parseInt(props.getProperty("proxyPort"))));
        }
        return proxy;
    }

    private static void loadDataFromFiles(final int typeLegalUnitId,
                                         final int cutId,
                                         final Logger logger,
                                         final String unzipPath,
                                         final Properties props) throws Exception {
        final FilenameFilter filter = (f, name) -> name.endsWith(".xlsx");
        final String[] files = new File(unzipPath).list(filter);
        if (files != null) {
            for (String file : files) {
                new ExcelDataLoader(typeLegalUnitId, cutId, logger).loadDataFile(unzipPath + "\\" + file,
                        Integer.parseInt(props.getProperty("countLoadThreads")));
            }
        }
    }

    private static String unzipFile(final Properties props, final String fileName) throws IOException {
        final String unzipPath = props.getProperty("downloadDir") + "\\" + fileName.split("\\.")[0];
        new UnzipUtility().unzip(props.getProperty("downloadDir") + "\\" + fileName, unzipPath);
        return unzipPath;
    }

    public static void main(String[] args) {
        try (final Connection connDB = ConnDB.getConnection()) {

            LogDB logDB = null;
            try {
                PropertyConfigurator.configure("log4j.properties");
                // Считываем настройки приложения
                final Properties props = getProperties();
                // Включаем логирование
                final Logger logger = getLogger(props);
                // Если необходимо используем прокси
                final Proxy proxy = getProxy(props);
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
                String fileName;
                String unzipPath;
                try (final Statement statement = connDB.createStatement();
                     final ResultSet resultSet = statement.executeQuery("SELECT id FROM stat_gov_kz.d_type_legal_unit WHERE is_updated = true")) {
                    while (resultSet.next()) {
                        typeLegalUnitId = resultSet.getInt("id");
                        try (final Statement statementOKED = connDB.createStatement();
                             final ResultSet resultSetOKED = statementOKED.executeQuery("SELECT item_id FROM stat_gov_kz.oked_list")) {
                            while (resultSetOKED.next()) {
                                okedId = resultSetOKED.getInt("item_id");
                                logger.log(Level.INFO, "loading data with okedID = " + okedId + ", typeLegalUnitID = " + typeLegalUnitId);
                                // скачиваем файл
                                fileName = new FileDownloader(proxy).getFile(cutId, typeLegalUnitId, sitCodes, okedId, katoId, props.getProperty("downloadDir"));
                                if (fileName == null) {
                                    continue;
                                }
                                // разархивируем файл
                                unzipPath = unzipFile(props, fileName);
                                // загружаем данные с файла(ов)
                                loadDataFromFiles(typeLegalUnitId, cutId, logger, unzipPath, props);
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
