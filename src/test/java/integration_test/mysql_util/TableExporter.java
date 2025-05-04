package integration_test.mysql_util;

import com.cogover.exception.DbException;
import com.cogover.template.server.config.Config;
import com.cogover.template.server.config.DbConfigLoader;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;

import java.io.File;
import java.util.Map;

/**
 * @author huydn on 3/8/24 11:45
 */
@Log4j2
public class TableExporter {

    public static void main(String[] args) throws IllegalAccessException, DbException {
        Config.loadConfig();
        DbConfigLoader.loadDbConfig();

        String workspaceId = "WSrSKqs3cAjYm";
        String recordId = "EWdG7uZJJFFKw";
        String insertDataFilePath = "src/test/resources/email_workspace_insert_data.sql";
        String deleteDataFilePath = "src/test/resources/email_workspace_delete_data.sql";

        MySqlExporter mySqlExporter = new MySqlExporter();
        mySqlExporter.setChangeNullNumberToZero(true);
        mySqlExporter.setChangeNullStringToEmpty(true);
        mySqlExporter.setWriteIdAsTemplate(true);//ghi ID thanh ${ID}
        mySqlExporter.setWorkspaceIdToReplace(workspaceId);

        String mysqlId = "MYSQL_APPLE";
        SessionFactory sessionFactory = DbConfigLoader.getSessionFactory(mysqlId, DbConfigLoader.DB_NAME_WORKSPACE);

        File file = new File(insertDataFilePath);
        if (file.exists()) {
            file.delete();
        }
        File file2 = new File(deleteDataFilePath);
        if (file2.exists()) {
            file2.delete();
        }

        //export email_workspace
        //nếu cần export các bản ghi liên quan ở các bảng khác thì tạo nhiều lệnh ntn
        mySqlExporter.getRecordsForExport(
                sessionFactory,
                "id=:recordId and workspace_id=:workspace_id",
                Map.of("recordId", recordId, "workspace_id", workspaceId),
                "email_workspace",
                null
        );

//        //export DepartmentPersonnel
//        mySqlExporter.getRecordsForExport(
//                sessionFactory,
//                "department_id=:department_id and workspace_id=:workspace_id",
//                Map.of("department_id", depId, "workspace_id", workspaceId),
//                "department_personnel",
//                List.of("department_id", "personnel_id")
//        );
//
//        //export DepartmentPosition
//        mySqlExporter.getRecordsForExport(
//                sessionFactory,
//                "department_id=:department_id and workspace_id=:workspace_id",
//                Map.of("department_id", depId, "workspace_id", workspaceId),
//                "department_position",
//                List.of("department_id", "position_id")
//        );

        mySqlExporter.exportDataToSQL(insertDataFilePath, deleteDataFilePath, true);
    }

}
