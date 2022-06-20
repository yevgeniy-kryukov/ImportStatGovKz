package kz.asoft.import_stat_gov_kz;

import org.json.JSONArray;

import java.net.Proxy;
import java.sql.*;

class Cut {
    private final Proxy proxy;
    private final Connection connDB;
    private Integer cutId = -1;

    Cut(Connection connDB, Proxy proxy)  {
        this.connDB = connDB;
        this.proxy = proxy;
    }

    Integer getCutId() throws Exception {
        if (this.cutId != -1) {
            return this.cutId;
        }

        // Получение списка срезов
        String jsonString = new HttpUtility(proxy).get("https://stat.gov.kz/api/rcut/ru");
        if (jsonString == null) {
            throw new Exception("Ошибка! Не удалось получить список срезов");
        }

        // Находим идентификатор последнего среза
        String cutName = "";
        JSONArray ja = new JSONArray(jsonString);
        for (int i = 0; i < ja.length(); i++) {
            int id = ja.getJSONObject(i).getInt("id");
            if (id > this.cutId) {
                this.cutId = id;
                cutName = ja.getJSONObject(i).getString("name");
            }
        }

        if (this.cutId == -1) {
            throw new Exception("Ошибка! Не удалось определить идентификатор последнего среза");
        }

        // Сохраняем срез
        if (!isExistsCut(this.cutId)) addCut(this.cutId, cutName);

        return this.cutId;
    }

    private boolean isExistsCut(int id) throws SQLException {
        final String sqlText = "SELECT 1 FROM stat_gov_kz.d_cut WHERE id = ?";
        try(final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addCut(int id, String name) throws SQLException {
        final String sqlText = "INSERT INTO stat_gov_kz.d_cut (id, name) VALUES (?, ?)";
        try(final PreparedStatement preparedStatement = connDB.prepareStatement(sqlText)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }
}
