package kz.asoft.import_stat_gov_kz;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExcelDataLoader {
    private final Connection conn;

    ExcelDataLoader(Connection conn) {
        this.conn = conn;
    }

    void saveData(String[] aRow, int cutId, int typeLegalUnitId) throws SQLException {
        final String sqlText = "INSERT INTO stat_gov_kz.g_legal (" +
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
                "gl_person_id" +
                ") " +
                "VALUES (?, ?, ?, to_date(?, 'dd.mm.yyyy'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT ON CONSTRAINT g_legal_iin_uq DO UPDATE SET " +
                "full_name_kz = EXCLUDED.full_name_kz," +
                "full_name = EXCLUDED.full_name," +
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
                "type_legal_unit_id = EXCLUDED.type_legal_unit_id";
        try(final PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {
            preparedStatement.setString(1, aRow[0]);
            preparedStatement.setString(2, aRow[1]);
            preparedStatement.setString(3, aRow[2]);
            preparedStatement.setString(4, aRow[3]);
            preparedStatement.setString(5, aRow[4]);
            preparedStatement.setString(6, aRow[5]);
            preparedStatement.setString(7, aRow[6]);
            preparedStatement.setString(8, aRow[7]);
            preparedStatement.setString(9, aRow[8]);
            preparedStatement.setString(10, aRow[9]);
            preparedStatement.setString(11, aRow[10]);
            preparedStatement.setString(12, aRow[11]);
            preparedStatement.setString(13, aRow[12]);
            preparedStatement.setString(14, aRow[13]);
            preparedStatement.setString(15, aRow[14]);
            preparedStatement.setString(16, aRow[15]);
            preparedStatement.setInt(17, cutId);
            preparedStatement.setInt(18, typeLegalUnitId);
            preparedStatement.setInt(19, -1);
            final int rowsCount = preparedStatement.executeUpdate();
        }
    }

    void loadDataFile(String fileLocation, int cutId, int typeLegalUnitId) throws Exception {
        String[] aRow = new String[16];

        InputStream is = Files.newInputStream(new File(fileLocation).toPath());
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        for (Sheet sheet : workbook) {
            System.out.println(sheet.getSheetName());
            rowloop:
            for (Row r : sheet) {
                if (r.getRowNum() == 1) {
                    break;
                }
                for (Cell c : r) {
                    if (c.getColumnIndex() == 0) { // в первой колонке должен быть ИИН (БИН)
                        try {
                            long cellValue = Long.parseLong(c.getStringCellValue());
                        } catch (NumberFormatException nfe) {
                            continue rowloop;
                        }
                    }
                    aRow[c.getColumnIndex()] = c.getStringCellValue();
                }
                saveData(aRow, cutId, typeLegalUnitId);
            }
        }
    }
}
