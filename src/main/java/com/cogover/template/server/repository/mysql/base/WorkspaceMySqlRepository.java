package com.cogover.template.server.repository.mysql.base;

import com.cogover.template.server.config.DbConfigLoader;

public abstract class WorkspaceMySqlRepository<T> extends MySqlRepository<T> {

    public WorkspaceMySqlRepository(String prefix, Class<T> entityClass) {
        super(
                DbConfigLoader.DB_NAME_WORKSPACE,
                prefix,
                entityClass
        );
    }

}
