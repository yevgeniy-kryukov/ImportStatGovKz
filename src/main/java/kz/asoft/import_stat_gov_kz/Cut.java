package kz.asoft.import_stat_gov_kz;

import org.json.JSONArray;

import java.net.Proxy;
import java.sql.*;

class Cut {

    static Integer getCutId(final Connection connDB, final Proxy proxy) throws Exception {
        int cutId = -1;

        // Получение списка срезов
        final String jsonString = new HttpUtility(proxy).get("https://stat.gov.kz/api/rcut/ru");
        if (jsonString == null) {
            throw new RuntimeException("Ошибка! Не удалось получить список срезов");
        }

        // Находим идентификатор последнего среза
        String cutName = "";
        final JSONArray ja = new JSONArray(jsonString);
        for (int i = 0; i < ja.length(); i++) {
            int id = ja.getJSONObject(i).getInt("id");
            if (id > cutId) {
                cutId = id;
                cutName = ja.getJSONObject(i).getString("name");
            }
        }

        if (cutId == -1) {
            throw new RuntimeException("Ошибка! Не удалось определить идентификатор актуального среза");
        }

        // Сохраняем срез
        if (!isExistsCut(connDB, cutId)) addCut(connDB, cutId, cutName);

        return cutId;
    }

    private static boolean isExistsCut(final Connection connDB, final int id) throws SQLException {
        final String sqlText = "SELECT 1 FROM stat_gov_kz.d_cut WHERE id = ?";
        try (final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void addCut(final Connection connDB, final int id, final String name) throws SQLException {
        final String sqlText = "INSERT INTO stat_gov_kz.d_cut (id, name) VALUES (?, ?)";
        try (final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }
}
