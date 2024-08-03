package kz.asoft.import_stat_gov_kz;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

class Legal {
    private final Connection connDB;

    private final int cutId;

    private final int typeLegalUnitId;

    Legal(int cutId, int typeLegalUnitId) throws Exception {
        this.connDB = ConnDB.getConnection();
        this.cutId = cutId;
        this.typeLegalUnitId = typeLegalUnitId;
    }

    private Long getGlPersonId(String iin_bin) throws SQLException {
        final String sqlText = "SELECT etl.etl_util_pkg.get_gl_person_id(?) as gl_person_id";
        try (final PreparedStatement preparedStatement = this.connDB.prepareStatement(sqlText)) {
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
        final String sqlText = "SELECT etl.etl_util_pkg.create_person_gl(?,?,?,?,?,?,?,?) as gl_person_id";
        try (final PreparedStatement preparedStatement = this.connDB.prepareStatement(sqlText)) {
            String surname = null;
            String name = null;
            String middlename = null;
            if (personName != null) {
                String[] aName = personName.split(" ");
                if (aName.length > 0) surname = aName[0];
                if (aName.length > 1) name = aName[1];
                if (aName.length > 2) middlename = aName[2];
            }
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

    void saveRow(String[] aRow) throws Exception {
        final String sqlText = "INSERT INTO stat_gov_kz.g_legal as g (" +
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
                    "kse_code, " +
                    "kse_name_kz, " +
                    "kse_name, " +
                    "kfs_code, " +
                    "kfs_name_kz, " +
                    "kfs_name, " +
                    "kato_code," +
                    "locality_name_kz," +
                    "locality_name," +
                    "legal_address," +
                    "leader_name," +
                    "cut_id," +
                    "type_legal_unit_id," +
                    "leader_gl_person_id," +
                    "actualization_dt," +
                    "is_actual," +
                    "leader_lname," +
                    "leader_fname," +
                    "leader_mname" +
                ") " +
                "VALUES (nextval('stat_gov_kz.g_legal_seq'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
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
                    "kse_code = EXCLUDED.kse_code," +
                    "kse_name_kz = EXCLUDED.kse_name_kz," +
                    "kse_name = EXCLUDED.kse_name," +
                    "kfs_code = EXCLUDED.kfs_code," +
                    "kfs_name_kz = EXCLUDED.kfs_name_kz," +
                    "kfs_name = EXCLUDED.kfs_name," +
                    "kato_code = EXCLUDED.kato_code," +
                    "locality_name_kz = EXCLUDED.locality_name_kz," +
                    "locality_name = EXCLUDED.locality_name," +
                    "legal_address = EXCLUDED.legal_address," +
                    "leader_name = EXCLUDED.leader_name," +
                    "cut_id = EXCLUDED.cut_id," +
                    "type_legal_unit_id = EXCLUDED.type_legal_unit_id," +
                    "actualization_dt = EXCLUDED.actualization_dt," +
                    "is_actual = EXCLUDED.is_actual," +
                    "leader_lname = EXCLUDED.leader_lname," +
                    "leader_fname = EXCLUDED.leader_fname," +
                    "leader_mname = EXCLUDED.leader_mname " +
                "WHERE g.cut_id <> EXCLUDED.cut_id";

        try (final PreparedStatement preparedStatement = this.connDB.prepareStatement(sqlText)) {
            String iinBin = aRow[0];
            if (iinBin.length() != 12) {
                return;
            }

            String leaderName = aRow[21].isEmpty() ? null : aRow[21];
            String leaderLname = null;
            String leaderFname = null;
            String leaderMname = null;

            if (leaderName != null) {
                String[] leaderNameParts = leaderName.split(" ");
                if (leaderNameParts.length > 0) leaderLname = leaderNameParts[0];
                if (leaderNameParts.length > 1) leaderFname = leaderNameParts[1];
                if (leaderNameParts.length > 2) leaderMname = leaderNameParts[2];
            }

            Long leaderGlPersonId = null;
            if (Integer.parseInt(iinBin.substring(4, 5)) < 4) {  // ИИН, только для физ.лиц
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
            preparedStatement.setObject(16, aRow[15].isEmpty() ? null : aRow[15], Types.VARCHAR);
            preparedStatement.setObject(17, aRow[16].isEmpty() ? null : aRow[16], Types.VARCHAR);

            preparedStatement.setObject(18, aRow[17].isEmpty() ? null : aRow[17], Types.VARCHAR);
            preparedStatement.setObject(19, aRow[18].isEmpty() ? null : aRow[18], Types.VARCHAR);
            preparedStatement.setObject(20, aRow[19].isEmpty() ? null : aRow[19], Types.VARCHAR);
            preparedStatement.setObject(21, aRow[20].isEmpty() ? null : aRow[20], Types.VARCHAR);
            preparedStatement.setObject(22, leaderName, Types.VARCHAR);
            preparedStatement.setObject(23, this.cutId, Types.INTEGER);
            preparedStatement.setObject(24, this.typeLegalUnitId, Types.INTEGER);
            preparedStatement.setObject(25, leaderGlPersonId, Types.NUMERIC);
            preparedStatement.setObject(26, LocalDateTime.now(), Types.TIMESTAMP);
            preparedStatement.setObject(27, true, Types.BOOLEAN);

            preparedStatement.setObject(28, leaderLname, Types.VARCHAR);
            preparedStatement.setObject(29, leaderFname, Types.VARCHAR);
            preparedStatement.setObject(30, leaderMname, Types.VARCHAR);

            final int rowsCount = preparedStatement.executeUpdate();
        }
    }

    void close() throws SQLException {
        this.connDB.close();
    }

    static void setNotActual(int cutId) throws Exception {
        final String sqlText = "UPDATE stat_gov_kz.g_legal SET is_actual = false WHERE cut_id < ?";
        try (final Connection connDB = ConnDB.getConnection();
             final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, cutId);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }
}
