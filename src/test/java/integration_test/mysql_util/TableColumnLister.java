package integration_test.mysql_util;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.config.DbConfigLoader;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huydn on 4/8/24 16:05
 */
@Log4j2
public class TableColumnLister {

    public static List<ColumnDetail> getListColumns(SessionFactory sessionFactory, String tableName) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        List<ColumnDetail> columnDetails = new ArrayList<>();

        try {
            session.doWork(connection -> {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet columns = metaData.getColumns(null, null, tableName, null);

                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    columnDetails.add(new ColumnDetail(columnName, columnType));

                    log.info("Column name: {}, Column type: {}", columnName, columnType);
                }

                columns.close();
            });
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            transaction.commit();
            session.close();
        }

        return columnDetails;
    }

    public static String buildSelectQuery(String tableName, List<ColumnDetail> columnDetails, String whereClause) {
        if (columnDetails == null || columnDetails.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder("SELECT ");

        for (int i = 0; i < columnDetails.size(); i++) {
            query.append("`");
            query.append(columnDetails.get(i).getName());
            query.append("`");
            if (i < columnDetails.size() - 1) {
                query.append(", ");
            }
        }

        query.append(" FROM ").append("`").append(tableName).append("`");

        query.append(" WHERE ").append(whereClause);

        return query.toString();
    }

}
