package kz.asoft.import_stat_gov_kz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

class JCut {
    private Integer id;

    private final Integer cutId;

    private final Integer typeLegalId;

    private final Integer okedItemId;

    private final Timestamp timeStart;

    private Timestamp timeEnd;

    private boolean isStarted = false;

    JCut(Integer cutId, Integer typeLegalId, Integer okedItemId) {
        this.cutId = cutId;
        this.typeLegalId = typeLegalId;
        this.okedItemId = okedItemId;
        this.timeStart = new Timestamp(System.currentTimeMillis());
        this.timeEnd = null;
    }

    void start() throws Exception {
        final String sqlText = "INSERT INTO stat_gov_kz.j_cut (id, cut_id, type_legal_id, oked_item_id, time_start) " +
                "VALUES (nextval('stat_gov_kz.j_cut_seq'), ?, ?, ?, ?) returning id";
        try (final Connection connDB = ConnDB.getConnection();
             final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, this.cutId);
            preparedStatement.setInt(2, this.typeLegalId);
            preparedStatement.setInt(3, this.okedItemId);
            preparedStatement.setTimestamp(4, this.timeStart);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.id = resultSet.getInt("id");
                    this.isStarted = true;
                }
            }
        }
    }

    void end() throws Exception {
        if (!this.isStarted) {
            return;
        }

        this.timeEnd = new Timestamp(System.currentTimeMillis());

        final String sqlText = "UPDATE stat_gov_kz.j_cut SET time_end = ? WHERE id = ?";
        try (final Connection connDB = ConnDB.getConnection();
             final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setTimestamp(1, this.timeEnd);
            preparedStatement.setInt(2, this.id);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }

    static int getLastLoadOKED(final int cutId, final int typeLegalId) throws Exception {
        int okedItemId = -1;
        try (final Connection connDB = ConnDB.getConnection();
             final PreparedStatement preparedStatement = connDB.prepareStatement("SELECT oked_item_id " +
                     "FROM stat_gov_kz.j_cut " +
                     "WHERE cut_id = ? AND type_legal_id = ? AND time_end IS NOT NULL " +
                     "ORDER BY time_end DESC " +
                     "LIMIT 1")) {
            preparedStatement.setInt(1, cutId);
            preparedStatement.setInt(2, typeLegalId);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    okedItemId = resultSet.getInt("oked_item_id");
                }
            }
        }
        return okedItemId;
    }
}
