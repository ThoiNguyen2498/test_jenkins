package com.cogover.template.server.config;

import com.cogover.template.server.database.entity.common_mysql.DbConfig;
import com.cogover.template.server.database.util.mysql.CommonHibernateUtil;
import com.cogover.template.server.database.util.mysql.WorkspaceHibernateUtil;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huydn on 15/5/24 15:29
 */
@Log4j2
public class DbConfigLoader {

    public static final int DB_TYPE_MYSQL = 1;
    public static final int DB_TYPE_MONGO = 2;
    public static final int DB_TYPE_ES = 3;

    public static final String MYSQL_COMMON = "MYSQL_COMMON";

    public static final String DB_NAME_ACCOUNT = "cogover_account";
    public static final String DB_NAME_WORKSPACE = "cogover_workspace";

    private static final Map<String, DbConfig> dbConfigs = new HashMap<>();
    private static final Map<String, SessionFactory> SESSION_FACTORIES = new HashMap<>();

    private DbConfigLoader() {
    }

    public static DbConfig get(String id) {
        return dbConfigs.get(id);
    }

    public static void loadDbConfig() {
        //lưu MYSQL_COMMON SessionFactory
        SESSION_FACTORIES.put(
                buildkey(MYSQL_COMMON, DB_NAME_ACCOUNT),
                CommonHibernateUtil.getSessionFactory()
        );

        try (Session session = CommonHibernateUtil.getSessionFactory().openSession()) {
            session.createQuery("from DbConfig", DbConfig.class).list().forEach(db -> {
                dbConfigs.put(db.getId(), db);
                log.info("DbConfig: {}", db.getId());

                //thuc hien tao sessionFactory cho cac dbConfig
                if (db.getType() == DB_TYPE_MYSQL) {
                    WorkspaceHibernateUtil factory = new WorkspaceHibernateUtil(
                            db.getId(),
                            db.getVaultKey(),
                            "config/hibernateWorkspace.cfg.xml"
                    );
                    SESSION_FACTORIES.put(
                            buildkey(db.getId(), DB_NAME_WORKSPACE),
                            factory.getSessionFactory()
                    );
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String buildkey(String mysqlId, String dbName) {
        return mysqlId + "_" + dbName;
    }

    public static SessionFactory getSessionFactory(String mysqlId, String dbName) {
        String key = buildkey(mysqlId, dbName);
        return SESSION_FACTORIES.get(key);
    }

}
