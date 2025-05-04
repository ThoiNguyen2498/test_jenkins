package com.cogover.template.server.database.util.mysql;

import com.cogover.vault.utils.VaultHibernateUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;

/**
 * @author huy@stringee.com
 */
@Log4j2
public class WorkspaceHibernateUtil {

    @Getter
    private SessionFactory sessionFactory;

    public WorkspaceHibernateUtil(String dbConfigId, String vaultCredentialsPath, String configFile) {
        try {
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure(new File(configFile));

            VaultHibernateUtils.settingVaultDbCredentials(builder, vaultCredentialsPath);
            VaultHibernateUtils.settingVaultDbAddr(
                    builder,
                    dbConfigId,
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
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

}
