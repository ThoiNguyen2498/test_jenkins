package integration_test.connect_to_db;

import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.database.entity.workspace_mysql.EmailWorkspace;
import com.cogover.template.server.database.util.mysql.CommonHibernateUtil;
import com.cogover.template.server.repository.mysql.workspace_db.WorkspaceEmailRepository;
import integration_test.InitResourceForTest;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author huydn on 18/5/24 01:51
 */
@Log4j2
class TestConnectToMySQL {

    @BeforeEach
    void setUp() {
        InitResourceForTest.init();
    }

    @Test
    void testConnect() {
        log.info("Test connect to MySQL...");

        //lay data tu common-mysql
        try (Session session = CommonHibernateUtil.getSessionFactory().openSession()) {
            int size = session.createQuery("from Account", Account.class).list().size();
            log.info("++++++++++++++++ List Account: {}", size);

            assertTrue(size > 0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }

    @Test
    void testInsertAndDelete() {
        WorkspaceEmailRepository repository = new WorkspaceEmailRepository();

        try {
            EmailWorkspace emailWorkspace = new EmailWorkspace();
            emailWorkspace.setId("id_test_1");
            emailWorkspace.setWorkspaceId("test-1");
            emailWorkspace.setCreated(System.currentTimeMillis());
            emailWorkspace.setUpdated(System.currentTimeMillis());
            emailWorkspace.setCreatedBy("test-1");
            emailWorkspace.setUpdatedBy("test-1");
            emailWorkspace.setDisplayName("test-1");
            emailWorkspace.setEmail("test-1@test-1");
            emailWorkspace.setOaToken("!");
            emailWorkspace.setOaExpired(System.currentTimeMillis());
            emailWorkspace.setSmtpHost("");
            emailWorkspace.setSmtpPort(1000);
            emailWorkspace.setStatus((byte) 1);
            emailWorkspace.setType((byte) 2);
            emailWorkspace.setOaRefreshToken("!");

            repository.insert("MYSQL_APPLE", emailWorkspace);
            log.info("Insert EmailWorkspace OK, ID: {}", emailWorkspace.getId());

            repository.delete("MYSQL_APPLE", emailWorkspace);
            log.info("Delete EmailWorkspace OK, ID: {}", emailWorkspace.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail();
        }
    }
}
