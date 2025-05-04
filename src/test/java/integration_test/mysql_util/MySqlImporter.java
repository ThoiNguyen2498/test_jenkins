package integration_test.mysql_util;

import com.cogover.template.server.util.ServerUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huydn on 4/8/24 13:54
 */
@Setter
@Log4j2
public class MySqlImporter {

    @Getter
    private Map<String, String> replaceValues =  new HashMap<>();

    /**
     * idTemplate -> ID
     */
    private Map<String, String> allIds = new HashMap<>();

    /**
     * tableName -> List<row id>
     */
    private Map<String, List<String>> insertedRowIds = new HashMap<>();

    public List<String> getInsertedRowIds(String tableName) {
        return insertedRowIds.get(tableName);
    }

    private List<String> findIdTemplate(String text) {
        String regex = "\\$\\{[A-Za-z0-9]{5,32}}";
        Matcher matcher = Pattern.compile(regex).matcher(text);
        List<String> foundIds = new ArrayList<>();
        while (matcher.find()) {
            foundIds.add(matcher.group());
        }

        return foundIds;
    }

    private String getTableName(String insertSql) {
        Pattern pattern = Pattern.compile(MySqlExporter.INSERT_SQL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(insertSql);
        // Tìm tên bảng
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public void importData(SessionFactory sessionFactory, String sqlFilePath) throws IOException {
        List<String> list = SqlFileUtils.readSQLFile(sqlFilePath);

        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();

            for (String sql : list) {
                List<String> foundIds = findIdTemplate(sql);
                for (String templateId : foundIds) {
                    if (!allIds.containsKey(templateId)) {
                        String newId = ServerUtil.randomString(12);
                        allIds.put(templateId, newId);
                    }
                    sql = sql.replace(templateId, allIds.get(templateId));
                }

                //gia tri dau tien luon la ID cua row: foundIds
                if (!foundIds.isEmpty()) {
                    String tableName = getTableName(sql);
                    String templateId = foundIds.getFirst();

                    if (!insertedRowIds.containsKey(tableName)) {
                        insertedRowIds.put(tableName, new ArrayList<>());
                    }
                    insertedRowIds.get(tableName).add(allIds.get(templateId));
                }

                log.debug("Found strings: {}, SQL={}", foundIds, sql);
                sql = replaceValues(sql);
                session.createNativeQuery(sql).executeUpdate();
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Error while importing data", e);
        }
    }

    private String replaceValues(String sql) {
        String newSql = sql;
        for (String oldValue : replaceValues.keySet()) {
            newSql = newSql.replace(oldValue, replaceValues.get(oldValue));
        }
        return newSql;
    }

    public void cleanData(SessionFactory sessionFactory, String sqlFilePath) throws IOException {
        List<String> list = SqlFileUtils.readSQLFile(sqlFilePath);

        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();

            for (String sql : list) {
                List<String> foundIds = findIdTemplate(sql);
                for (String templateId : foundIds) {
                    sql = sql.replace(templateId, allIds.get(templateId));
                }

                log.debug("Found strings 2: {}, SQL={}", foundIds, sql);
                sql = replaceValues(sql);
                session.createNativeQuery(sql).executeUpdate();
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("Error while importing data", e);
        }
    }

    public void setWorkspaceId(String workspaceId) {
        replaceValues.put(MySqlExporter.WORKSPACE_ID, workspaceId);
    }
}
