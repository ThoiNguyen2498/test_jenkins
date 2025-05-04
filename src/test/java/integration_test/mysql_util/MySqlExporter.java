package integration_test.mysql_util;

import com.cogover.exception.DbException;
import com.cogover.template.server.util.ServerUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author huydn on 2/8/24 23:11
 */
@Log4j2
public class MySqlExporter {

    private static final Map<String, Boolean> COLUMN_TYPE_AS_STRING = Map.of(
            "VARCHAR", true,
            "TEXT", true,
            "CHAR", true,
            "LONGTEXT", true,
            "MEDIUMTEXT", true,
            "TINYTEXT", true
    );
    private static final Map<String, Boolean> COLUMN_TYPE_AS_NUMBER = Map.of(
            "INT", true,
            "TINYINT", true,
            "BIGINT", true,
            "BIT", true
    );


    private static final String INSERT_SQL = "INSERT INTO `%s` (%s) VALUES (%s);";
    public static final String INSERT_SQL_REGEX = "INSERT INTO `([a-zA-Z0-9_]+)`";//muc dich de lay ten bang tu: INSERT INTO `table_name`

    private static final String DELETE_SQL = "DELETE FROM `%s` WHERE %s;";

    @Getter
    private Map<String, String> replaceValues = new HashMap<>();

    static final String WORKSPACE_ID = "${WS_ID}";

    @Getter
    private final List<String> columnIdNames = List.of("id");

    /**
     * dbTableName -> columnNames
     */
    private final Map<String, List<ColumnDetail>> tableColumnNames = new HashMap<>();

    /**
     * dbTableName -> CompositeIds columns
     */
    private final Map<String, List<String>> tableCompositeIds = new HashMap<>();

    @Setter
    private boolean writeIdAsTemplate = true;

    /**
     * dbTableName -> records
     */
    private final Map<String, List<Object[]>> allRecords = new HashMap<>();

    /**
     * oldId -> newId
     */
    private final Map<String, String> idValues = new HashMap<>();

    private final Map<Integer, Boolean> alreadyRegenIds = new HashMap<>();

    @Setter
    private boolean changeNullStringToEmpty = true;

    @Setter
    private boolean changeNullNumberToZero = true;

    public MySqlExporter() {
    }

    public void regenerateId() {
        //quet tang dan
        for (int i = 0; i < allRecords.keySet().size(); i++) {
            String tableName = (String) allRecords.keySet().toArray()[i];
            List<Object[]> records = allRecords.get(tableName);
            for (Object[] record : records) {
                regenerateId(tableName, record);
            }
        }
        //quet giam dan
        for (int i = allRecords.keySet().size() - 1; i >= 0; i--) {
            String tableName = (String) allRecords.keySet().toArray()[i];
            List<?> records = allRecords.get(tableName);
            for (Object record1 : records) {
                Object[] record = (Object[]) record1;
                regenerateId(tableName, record);
            }
        }
    }

    public void regenerateId(String tableName, Object[] record) {
        List<ColumnDetail> columnNames = tableColumnNames.get(tableName);

        for (int i = 0; i < columnNames.size(); i++) {
            ColumnDetail column = columnNames.get(i);

            String columnName = column.getName();
            String columnType = column.getType();
            Object columnValue = record[i];

            if (columnValue == null) {
                if (changeNullStringToEmpty && COLUMN_TYPE_AS_STRING.containsKey(columnType.toUpperCase())) {
                    columnValue = "";
                }

                if (changeNullNumberToZero && COLUMN_TYPE_AS_NUMBER.containsKey(columnType.toUpperCase())) {
                    columnValue = 0;
                }

                if (columnValue == null)
                    continue;
            }

            if (columnIdNames.contains(columnName)) {//neu day la cot ID
                //tao moi ID neu chua tao moi
                if (!alreadyRegenIds.containsKey(record.hashCode())) {
                    String oldValue = columnValue.toString();

                    //gen new ID
                    columnValue = ServerUtil.randomString(9);
                    if (writeIdAsTemplate) {
                        columnValue = String.format("${%s}", columnValue);
                    }

                    //set new
                    record[i] = columnValue;

                    //put old ID -> new ID
                    idValues.put(oldValue, columnValue.toString());

                    alreadyRegenIds.put(record.hashCode(), true);
                }
            } else {
                //kiem tra gia tri cot khac co trung vs ID ko?
                if (columnValue instanceof String value) {
                    if (idValues.containsKey(value)) {
                        columnValue = idValues.get(value);

                        //set new
                        record[i] = columnValue;
                    }
                }
            }

            replaceValue(record, i);
        }
    }

    private void replaceValue(Object[] record, int i) {
        if (record[i] == null) {
            return;
        }

        String currentValue = record[i].toString();
        if (replaceValues.containsKey(currentValue)) {
            record[i] = currentValue.replace(currentValue, replaceValues.get(currentValue));
        }
    }

    public String buildInsertOrDeleteSQL(Object[] record, String dbTableName, boolean isInsert) throws IllegalAccessException {
        StringJoiner columnNames = new StringJoiner(", ");
        StringJoiner columnValues = new StringJoiner(", ");

        StringBuilder deleteWhere = new StringBuilder("1");

        List<String> compositeIds = tableCompositeIds.get(dbTableName);

        List<ColumnDetail> columnNames2 = tableColumnNames.get(dbTableName);
        for (int i = 0; i < columnNames2.size(); i++) {
            ColumnDetail column = columnNames2.get(i);

            String columnName = column.getName();
            String columnType = column.getType();
            Object columnValue = record[i];

            if (columnValue == null) {
                continue;
            }

            if (columnIdNames.contains(columnName) || compositeIds.contains(columnName)) {//neu la composite id hoac ID
                deleteWhere
                        .append(" AND `")
                        .append(columnName)
                        .append("` = '")
                        .append(columnValue)
                        .append("'");
            }

            columnNames.add(String.format("`%s`", columnName));

            try {
                if (columnValue instanceof String) {
                    columnValues.add(String.format("'%s'", columnValue));
                } else {
                    columnValues.add(columnValue.toString());
                }
            } catch (Exception e) {
                log.error("++++++ columnName: {}", columnName);
                log.error("++++++ value: {}", columnValue.toString());
                log.error(e, e);
                System.exit(1);
            }
        }

        if (isInsert) {
            return String.format(INSERT_SQL, dbTableName, columnNames, columnValues);
        }

        return String.format(DELETE_SQL, dbTableName, deleteWhere);
    }

    public void getRecordsForExport(SessionFactory sessionFactory, String where, Map<String, Object> params, String dbTableName, List<String> compositeIds) throws DbException {
        List<ColumnDetail> columnNames = TableColumnLister.getListColumns(sessionFactory, dbTableName);
        tableColumnNames.put(dbTableName, columnNames);

        //compositeIds
        if (compositeIds == null) {
            compositeIds = new ArrayList<>();
        }
        tableCompositeIds.put(dbTableName, compositeIds);

        String selectQuery = TableColumnLister.buildSelectQuery(dbTableName, columnNames, where);
        log.info("selectQuery: {}", selectQuery);

        try (Session session = sessionFactory.openSession()) {
            Query<?> query = session.createNativeQuery(selectQuery);
            params.keySet().forEach(key -> query.setParameter(key, params.get(key)));
            List<Object[]> list = (List<Object[]>) query.list();
            allRecords.put(dbTableName, list);
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e.getCause());
        }
    }

    public void exportDataToSQL(
            String insertDataFilePath,
            String deleteDataFilePath,
            boolean regenerateId) {
        //neu regenerateId = true va la id thi tao id moi
        if (regenerateId) {
            regenerateId();
        }

        // Ghi dữ liệu vào file insert .sql
        processWrite(true, insertDataFilePath);
        processWrite(false, deleteDataFilePath);
    }

    private void processWrite(boolean isInsert, String fileName) {
        // Ghi dữ liệu vào file delete .sql
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = allRecords.keySet().size() - 1; i >= 0; i--) {
                String tableName = (String) allRecords.keySet().toArray()[i];
                List<Object[]> records = allRecords.get(tableName);

                for (Object[] record : records) {
                    String sqlInsert = buildInsertOrDeleteSQL(record, tableName, isInsert);
                    log.debug("Write SQL to delete file: {}", sqlInsert);
                    writer.write(sqlInsert);
                    writer.newLine();
                }
            }
        } catch (IOException | IllegalAccessException e) {
            log.error(e, e);
        }
    }

    public void setWorkspaceIdToReplace(String workspaceIdToReplace) {
        replaceValues.put(workspaceIdToReplace, WORKSPACE_ID);
    }
}
