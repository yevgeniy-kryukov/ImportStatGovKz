package kz.asoft.import_stat_gov_kz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class SitCode {

    static String getAllCodes() throws Exception {
        String codes = "";
        final String sqlText = "SELECT string_agg(id::character varying, ','  ORDER BY id) as lst " +
                                "FROM stat_gov_kz.d_situational_code " +
                                "WHERE is_updated = true and is_in_group_active = true";
        try (final Connection connDB = ConnDB.getConnection();
             final Statement statement = connDB.createStatement();
             final ResultSet resultSet = statement.executeQuery(sqlText)) {
            while (resultSet.next()) {
                codes = resultSet.getString("lst");
            }
        }
        return codes;
    }
}
