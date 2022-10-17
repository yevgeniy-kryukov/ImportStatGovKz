package kz.asoft.import_stat_gov_kz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class SitCode {

    static String getAllCodes(Connection connDB) throws SQLException {
        String codes = "";
        final String sqlText = "SELECT string_agg(id::character varying, ',') as lst " +
                                "FROM stat_gov_kz.d_situational_code " +
                                "WHERE is_updated = true";
        try (Statement statement = connDB.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlText)) {
            while (resultSet.next()) {
                codes = resultSet.getString("lst");
            }
        }
        return codes;
    }
}
