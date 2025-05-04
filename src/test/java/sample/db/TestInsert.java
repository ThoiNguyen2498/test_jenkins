package sample.db;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.database.entity.common_mysql.DbConfig;
import com.cogover.template.server.database.util.mysql.CommonHibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * @author huydn on 07/04/2024 03:30
 */
public class TestInsert {

    private final static Logger log = LogManager.getLogger("TestInsert");

    public static void main(String[] args) {
        Config.loadConfig();

        long tttt = System.currentTimeMillis() / 1000;

        DbConfig dbConfig = new DbConfig();
        dbConfig.setId("a" + tttt);
        dbConfig.setType((byte) 1);
        dbConfig.setStatus(true);

        Transaction transaction = null;
        try (Session session = CommonHibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(dbConfig);
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        //

        try (Session session = CommonHibernateUtil.getSessionFactory().openSession()) {
            List<DbConfig> books = session.createQuery("from DbConfig", DbConfig.class).list();
            books.forEach(b -> {
                log.info("DbConfig: {}", b.getId());
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
