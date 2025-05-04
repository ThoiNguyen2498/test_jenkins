package com.cogover.template.server.database.util.mysql;

import com.cogover.vault.utils.VaultHibernateUtils;
import com.cogover.vault.utils.VaultUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.json.JSONObject;

import java.io.File;

/**
 * @author huy@stringee.com
 */
@Log4j2
public class CommonHibernateUtil {

    @Getter
    private static SessionFactory sessionFactory = buildSessionFactory();

    private CommonHibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            if (sessionFactory == null) {
                File configFile = new File("config/hibernate.cfg.xml");

                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure(configFile);

                JSONObject json = VaultUtil.getModuleAddr("mysql-common");
                builder.getSettings().put("hibernate.connection.username", json.getString("username"));
                builder.getSettings().put("hibernate.connection.password", json.getString("password"));

                VaultHibernateUtils.settingVaultDbAddr(
                        builder,
                        "mysql-common",
                        "FROM_VAULT",
                        true
                );

                //thay {NAMESPACE} => branch name
                String gitBranch = System.getenv().get("GIT_BRANCH");
                if (gitBranch != null && !gitBranch.isEmpty()) {
                    String currentUrl = (String) builder.getSettings().get("hibernate.connection.url");
                    String newUrl = currentUrl.replace("{NAMESPACE}", gitBranch);
                    builder.getSettings().put("hibernate.connection.url", newUrl);
                }

                Metadata metaData = new MetadataSources(builder.build())
                        .getMetadataBuilder()
                        .build();
                sessionFactory = metaData.getSessionFactoryBuilder().build();
            }
            return sessionFactory;
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

}
