package kz.asoft.import_stat_gov_kz;

import java.sql.*;

public class Cut {
    private final Connection conn;

    Cut(Connection conn)  {
        this.conn = conn;
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

    void addCut(int id, String name) throws SQLException {
        final String sqlText = "INSERT INTO stat_gov_kz.d_cut (id, name) VALUES (?, ?)";
        try(final PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }
}
