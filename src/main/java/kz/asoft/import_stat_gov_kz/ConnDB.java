package kz.asoft.import_stat_gov_kz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;
import java.util.Properties;

class ConnDB {
    static private Connection connDB = null;

    private ConnDB() {}

    static Connection getConnection() throws Exception {
        if (connDB == null) {
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
            final Properties props = new Properties();
            try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
                props.load(in);
            }
            final String url = props.getProperty("url");
            final String username = props.getProperty("username");
            final String password = props.getProperty("password");

            connDB = DriverManager.getConnection(url, username, password);
        }
        return connDB;
    }
}
