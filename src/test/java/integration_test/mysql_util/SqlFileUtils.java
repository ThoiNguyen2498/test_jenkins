package integration_test.mysql_util;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huydn on 2/8/24 22:17
 */
@Log4j2
public class SqlFileUtils {

    public static List<String> readSQLFile(String filePath) throws IOException {
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Bỏ qua các dòng comment và dòng trống
                if (line.startsWith("--") || line.isEmpty()) {
                    continue;
                }

                // Thêm dòng hiện tại vào câu lệnh SQL
                sql.append(line);

                // Nếu dòng kết thúc bằng dấu chấm phẩy, đây là kết thúc của một câu lệnh SQL
                if (line.endsWith(";")) {
                    sqlStatements.add(sql.toString());
                    sql = new StringBuilder();
                }
            }
        }

        return sqlStatements;
    }

    public static void executeSQLFile(Session session, String filePath) throws IOException {
        List<String> list = readSQLFile(filePath);

        for (String sql : list) {
            log.info("Executing SQL: {}", sql);
            session.createNativeQuery(sql).executeUpdate();
        }
    }

}
