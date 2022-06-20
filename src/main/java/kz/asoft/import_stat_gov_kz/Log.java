package kz.asoft.import_stat_gov_kz;

import java.sql.*;
import java.text.SimpleDateFormat;

public class Log {
    private final Connection conn;

    private Integer id;

    private final Integer cutId;

    private boolean isStarted = false;

    Log(Connection conn, Integer cutId)  {
        this.conn = conn;
        this.cutId = cutId;
    }

    boolean start() throws SQLException {
        if (isExistsUnfinishedProcess()) {
            return false;
        }

        final String sqlText = "INSERT INTO stat_gov_kz.j_loader (id, started, cut_id) " +
                                "VALUES (nextval('stat_gov_kz.j_cut_loader_seq'), localtimestamp, ?) returning id";
        try(final PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, cutId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.id = resultSet.getInt("id");
                    this.isStarted = true;
                }
            }
        }

        return true;
    }

    void finish(String errorText) throws SQLException {
        if (!this.isStarted) {
            return;
        }
        final String sqlText = "UPDATE stat_gov_kz.j_loader SET finished = localtimestamp, error_text = ? WHERE id = ?";
        try(final PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {
            preparedStatement.setString(1, errorText);
            preparedStatement.setInt(2, id);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }

    private boolean isExistsUnfinishedProcess() throws SQLException {
        final String sqlText = "SELECT id FROM stat_gov_kz.j_loader WHERE finished IS NULL LIMIT 1";
        try(Statement statement = this.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlText);)
        {
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }
}
