package kz.asoft.import_stat_gov_kz;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

class Legal {
    private final Connection connDB;

    private final int cutId;

    Legal(Connection connDB, int cutId)  {
        this.connDB = connDB;
        this.cutId = cutId;
    }

    private Long getGlPersonId(String iin_bin) throws SQLException {
        final String sqlText = "SELECT etl_util_pkg.get_gl_person_id(?) as gl_person_id";
        try(final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setString(1, iin_bin);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getObject("gl_person_id") != null ? resultSet.getLong("gl_person_id") : null;
                }
            }
        }
        return null;
    }

    private Long createGlPersonId(String iin_bin, String personName) throws SQLException {
        final int hDBSourceId = 69;
        final int hCountryId = 105;
        final String sqlText = "SELECT etl_util_pkg.create_person_gl(?,?,?,?,?,?,?,?) as gl_person_id";
        try(final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            String[] aName = personName.split(" ");
            String surname = "";
            String name = "";
            String middlename = "";
            if (aName.length > 0) surname = aName[0];
            if (aName.length > 1) name = aName[1];
            if (aName.length > 2) middlename = aName[2];
            preparedStatement.setString(1, iin_bin);
            preparedStatement.setString(2, surname);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, middlename);
            preparedStatement.setTimestamp(5, null);
            preparedStatement.setString(6, null);
            preparedStatement.setInt(7, hCountryId);
            preparedStatement.setInt(8, hDBSourceId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getObject("gl_person_id") != null ? resultSet.getLong("gl_person_id") : null;
                }
            }
        }
        return null;
    }

    void saveData(int typeLegalUnitId, String[] aRow) throws Exception {
        final String sqlText = "INSERT INTO stat_gov_kz.g_legal (" +
                "id," +
                "bin_iin," +
                "full_name_kz," +
                "full_name," +
                "date_reg," +
                "oked_main_code," +
                "oked_main_activity_name_kz," +
                "oked_main_activity_name," +
                "secondary_oked_code_list," +
                "krp_code," +
                "krp_name_kz," +
                "krp_name," +
                "kato_code," +
                "locality_name_kz," +
                "locality_name," +
                "legal_address," +
                "leader_name," +
                "cut_id," +
                "type_legal_unit_id," +
                "leader_gl_person_id," +
                "actualization_dt," +
                "is_actual" +
                ") " +
                "VALUES (nextval('stat_gov_kz.g_legal_seq'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT ON CONSTRAINT g_legal_bin_iin_uk DO UPDATE SET " +
                "full_name_kz = EXCLUDED.full_name_kz," +
                "full_name = EXCLUDED.full_name," +
                "date_reg = EXCLUDED.date_reg," +
                "oked_main_code = EXCLUDED.oked_main_code," +
                "oked_main_activity_name_kz = EXCLUDED.oked_main_activity_name_kz," +
                "oked_main_activity_name = EXCLUDED.oked_main_activity_name," +
                "secondary_oked_code_list = EXCLUDED.secondary_oked_code_list," +
                "krp_code = EXCLUDED.krp_code," +
                "krp_name_kz = EXCLUDED.krp_name_kz," +
                "krp_name = EXCLUDED.krp_name," +
                "kato_code = EXCLUDED.kato_code," +
                "locality_name_kz = EXCLUDED.locality_name_kz," +
                "locality_name = EXCLUDED.locality_name," +
                "legal_address = EXCLUDED.legal_address," +
                "leader_name = EXCLUDED.leader_name," +
                "cut_id = EXCLUDED.cut_id," +
                "type_legal_unit_id = EXCLUDED.type_legal_unit_id," +
                "actualization_dt = EXCLUDED.actualization_dt," +
                "is_actual = EXCLUDED.is_actual";
        try(final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            String iinBin = aRow[0];
            if (iinBin.length()!=12) {
                return;
            }

            String leaderName = aRow[15].isEmpty() ? "-" : aRow[15];

            Long leaderGlPersonId = null;
            if (Integer.parseInt(iinBin.substring(4,5)) < 4) {  // ИИН, только для физ.лиц
                leaderGlPersonId = getGlPersonId(iinBin);
                if (leaderGlPersonId == null) leaderGlPersonId = createGlPersonId(iinBin, leaderName);
            }

            Date dateReg = null;
            if (!aRow[3].isEmpty()) {
                dateReg = new SimpleDateFormat("dd.MM.yyyy").parse(aRow[3]);
            }

            preparedStatement.setObject(1, iinBin, Types.VARCHAR);
            preparedStatement.setObject(2, aRow[1].isEmpty() ? null : aRow[1], Types.VARCHAR);
            preparedStatement.setObject(3, aRow[2].isEmpty() ? null : aRow[2], Types.VARCHAR);
            preparedStatement.setObject(4, dateReg, Types.TIMESTAMP);
            preparedStatement.setObject(5, aRow[4].isEmpty() ? null : aRow[4], Types.VARCHAR);
            preparedStatement.setObject(6, aRow[5].isEmpty() ? null : aRow[5], Types.VARCHAR);
            preparedStatement.setObject(7, aRow[6].isEmpty() ? null : aRow[6], Types.VARCHAR);
            preparedStatement.setObject(8, aRow[7].isEmpty() ? null : aRow[7], Types.VARCHAR);
            preparedStatement.setObject(9, aRow[8].isEmpty() ? null : aRow[8], Types.VARCHAR);
            preparedStatement.setObject(10, aRow[9].isEmpty() ? null : aRow[9], Types.VARCHAR);
            preparedStatement.setObject(11, aRow[10].isEmpty() ? null : aRow[10], Types.VARCHAR);
            preparedStatement.setObject(12, aRow[11].isEmpty() ? null : aRow[11], Types.VARCHAR);
            preparedStatement.setObject(13, aRow[12].isEmpty() ? null : aRow[12], Types.VARCHAR);
            preparedStatement.setObject(14, aRow[13].isEmpty() ? null : aRow[13], Types.VARCHAR);
            preparedStatement.setObject(15, aRow[14].isEmpty() ? null : aRow[14], Types.VARCHAR);
            preparedStatement.setObject(16, leaderName, Types.VARCHAR);
            preparedStatement.setObject(17, cutId, Types.INTEGER);
            preparedStatement.setObject(18, typeLegalUnitId, Types.INTEGER);
            preparedStatement.setObject(19, leaderGlPersonId, Types.NUMERIC);
            preparedStatement.setObject(20, LocalDateTime.now(), Types.TIMESTAMP);
            preparedStatement.setObject(21, true, Types.BOOLEAN);

            final int rowsCount = preparedStatement.executeUpdate();
        }
    }

    void setNotActual() throws SQLException {
        final String sqlText = "UPDATE stat_gov_kz.g_legal SET is_actual = false WHERE cut_id < ?";
        try(final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, cutId);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }
}
