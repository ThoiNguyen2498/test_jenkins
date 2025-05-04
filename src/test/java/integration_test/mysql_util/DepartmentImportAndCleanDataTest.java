package integration_test.mysql_util;

import com.cogover.exception.DbException;
import com.cogover.template.server.config.Config;
import com.cogover.template.server.config.DbConfigLoader;
import com.cogover.template.server.database.entity.workspace_mysql.EmailWorkspace;
import com.cogover.template.server.repository.mysql.workspace_db.WorkspaceEmailRepository;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author huydn on 4/8/24 17:26
 */
@Log4j2
class DepartmentImportAndCleanDataTest {

    private final MySqlImporter mySqlImporter = new MySqlImporter();
    private String emailWorkspaceId;
    private SessionFactory sessionFactory;
    private static final String MYSQL_ID = "MYSQL_APPLE";
    private boolean doCleanImportedDataAfterTests = true;

    @BeforeEach
    void setUp() throws IOException, DbException {
        Config.loadConfig();
        DbConfigLoader.loadDbConfig();
        sessionFactory = DbConfigLoader.getSessionFactory(MYSQL_ID, DbConfigLoader.DB_NAME_WORKSPACE);

        String workspaceId = "WSiiIfNB6k";

        //import
        mySqlImporter.setWorkspaceId(workspaceId);
        mySqlImporter.importData(
                sessionFactory,
                "src/test/resources/email_workspace_insert_data.sql"
        );
        emailWorkspaceId = mySqlImporter.getInsertedRowIds("email_workspace").getFirst();
        log.info("Auto generated emailWorkspaceId: {}", emailWorkspaceId);
        //test
        WorkspaceEmailRepository repository = new WorkspaceEmailRepository();
        EmailWorkspace emailWorkspace = repository.findOneById(MYSQL_ID, emailWorkspaceId);
        assertNotNull(emailWorkspace);
    }

    @AfterEach
    void tearDown() throws IOException, DbException {
        //clean
        if (doCleanImportedDataAfterTests) {
            mySqlImporter.cleanData(
                    sessionFactory,
                    "src/test/resources/email_workspace_delete_data.sql"
            );
        }
        //test
        WorkspaceEmailRepository departmentRepository = new WorkspaceEmailRepository();
        EmailWorkspace dep = departmentRepository.findOneById(MYSQL_ID, emailWorkspaceId);

        assertEquals(dep == null, doCleanImportedDataAfterTests);
    }

    @Test
    void test1() throws DbException {
        doCleanImportedDataAfterTests = true;

        log.info("departmentId: {}", emailWorkspaceId);
        assertNotNull(emailWorkspaceId);

        WorkspaceEmailRepository departmentRepository = new WorkspaceEmailRepository();
        EmailWorkspace dep = departmentRepository.findOneById(MYSQL_ID, emailWorkspaceId);
        assertNotNull(dep);
    }

}
