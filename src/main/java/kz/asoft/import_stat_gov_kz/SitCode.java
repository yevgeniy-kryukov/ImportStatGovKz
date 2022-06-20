package kz.asoft.import_stat_gov_kz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class SitCode {
    private final Connection connDB;

    SitCode(Connection connDB)  {
        this.connDB = connDB;
    }

    String getAllCodes() throws SQLException {
        String codes = "";
        final String sqlText = "SELECT string_agg(id, ',') as lst FROM stat_gov_kz.d_situational_codes WHERE is_updated = true";
        try (Statement statement = connDB.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlText)) {
            while (resultSet.next()) {
                codes = resultSet.getString("lst");
            }
        }
        return codes;
    }
}
