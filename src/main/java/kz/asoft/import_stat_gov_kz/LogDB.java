package kz.asoft.import_stat_gov_kz;

import java.sql.*;

class LogDB {
    private final Connection connDB;

    private Integer id;

    private final Integer cutId;

    private boolean isStarted = false;

    LogDB(Connection connDB, Integer cutId) {
        this.connDB = connDB;
        this.cutId = cutId;
    }

    boolean start() throws SQLException {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (isExistsUnfinishedProcess()) {
            System.out.println(timestamp + " Имеются не завершенные процессы загрузки в журнале. Загрузка отменена!");
            return false;
        }

        if (isLoadedCut(this.cutId)) {
            System.out.println(timestamp + " Актуальный срез уже загружен. Загрузка отменена!");
            return false;
        }

        final String sqlText = "INSERT INTO stat_gov_kz.j_loader (id, started, cut_id) " +
                "VALUES (nextval('stat_gov_kz.j_loader_seq'), localtimestamp, ?) returning id";
        try (final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, cutId);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.id = resultSet.getInt("id");
                    this.isStarted = true;
                }
            }
        }

        return true;
    }

    void finish(final String errorText) throws SQLException {
        if (!this.isStarted) {
            return;
        }

        final String sqlText = "UPDATE stat_gov_kz.j_loader SET finished = localtimestamp, error_text = ? WHERE id = ?";
        try (final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setString(1, errorText);
            preparedStatement.setInt(2, id);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }

    private boolean isExistsUnfinishedProcess() throws SQLException {
        final String sqlText = "SELECT id FROM stat_gov_kz.j_loader WHERE finished IS NULL LIMIT 1";
        try (final Statement statement = this.connDB.createStatement();
             final ResultSet resultSet = statement.executeQuery(sqlText)) {
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    private boolean isLoadedCut(final int cutId) throws SQLException {
        final String sqlText = "SELECT id " +
                "FROM stat_gov_kz.j_loader " +
                "WHERE finished IS NOT NULL and error_text IS NULL and cut_id = ?" +
                "LIMIT 1";
        try (final PreparedStatement preparedStatement = this.connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, cutId);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }
}
