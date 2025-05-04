package sample.db;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.database.entity.workspace_mysql.EmailWorkspace;
import com.cogover.template.server.database.util.mysql.CommonHibernateUtil;
import com.cogover.template.server.database.util.mysql.WorkspaceHibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

/**
 * Neu su dung Vault, can set bien moi truong de chay
 * ENV_VAULT_TYPE=0;ENV_HASHICORP_VAULT_VERSION=2;ENV_HASHICORP_VAULT_ADDRESS=http://127.0.0.1:8200;ENV_HASHICORP_VAULT_TOKEN=123456
 *
 * @author huydn on 06/04/2024 14:33
 */
public class Test1 {

    private final static Logger log = LogManager.getLogger("Test1");

    public static void main(String[] args) {
        Config.loadConfig();

        //lay data tu common-mysql
        try (Session session = CommonHibernateUtil.getSessionFactory().openSession()) {
            int size = session.createQuery("from Account", Account.class).list().size();
            log.info("++++++++++++++++ List Account: {}", size);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        //lay data tu workspace-mysql
        WorkspaceHibernateUtil wsHibernateUtil = new WorkspaceHibernateUtil("MYSQL_APPLE", "kv/db/cogover", "config/hibernateWorkspace.cfg.xml");
        try (Session session = wsHibernateUtil.openSession()) {
            int size = session.createQuery("from EmailWorkspace", EmailWorkspace.class).list().size();
            log.info("++++++++++++++++ List EmailWorkspace 1: {}", size);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        //lay data tu workspace-mysql
        wsHibernateUtil = new WorkspaceHibernateUtil("MYSQL_ORANGE", "kv/db/cogover", "config/hibernateWorkspace.cfg.xml");
        try (Session session = wsHibernateUtil.openSession()) {
            int size = session.createQuery("from EmailWorkspace", EmailWorkspace.class).list().size();
            log.info("++++++++++++++++ List EmailWorkspace 2: {}", size);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
