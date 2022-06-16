package kz.asoft.import_stat_gov_kz_sole_trader;

import java.sql.*;
import java.text.SimpleDateFormat;

public class Log {
    private final Connection conn;

    Log(Connection conn) {
        this.conn = conn;
    }

    int startProcess(int cutId, int typeLegalUnitId) throws SQLException {
        final String sqlText = "INSERT INTO stat_gov_kz.j_cut_loader (id, started, cut_id, type_legal_unit_id) " +
                                "VALUES (nextval('stat_gov_kz.j_cut_loader_seq'), localtimestamp, ?, ?) returning id";
        try(final PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, cutId);
            preparedStatement.setInt(2, typeLegalUnitId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return 0;
    }

    boolean finishProcess(int id, String errorText) throws SQLException {
        final String sqlText = "UPDATE stat_gov_kz.j_cut_loader SET finished = localtimestamp, error_text = ? WHERE id = ?";
        try(final PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {
            preparedStatement.setString(1, errorText);
            preparedStatement.setInt(2, id);
            final int rowsCount = preparedStatement.executeUpdate();
            if (rowsCount > 0) {
                return true;
            }
        }
        return false;
    }

    boolean isExistsUnfinishedProcess() throws SQLException {
        final String sqlText = "SELECT id FROM stat_gov_kz.j_cut_loader WHERE finished IS NULL LIMIT 1";
        try(Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlText);)
        {
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    boolean isExistsCut(int id) throws SQLException {
        final String sqlText = "SELECT 1 FROM stat_gov_kz.d_cut WHERE id = ?";
        try(final PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean addCut(int id, String name) throws SQLException {
        final String sqlText = "INSERT INTO stat_gov_kz.d_cut (id, name) VALUES (?, ?)";
        try(final PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            final int rowsCount = preparedStatement.executeUpdate();
            if (rowsCount > 0) {
                return true;
            }
        }
        return false;
    }
}
