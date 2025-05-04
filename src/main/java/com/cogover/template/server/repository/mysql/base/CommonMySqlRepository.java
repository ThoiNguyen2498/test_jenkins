package com.cogover.template.server.repository.mysql.base;

import com.cogover.template.server.config.DbConfigLoader;

public abstract class CommonMySqlRepository<T> extends MySqlRepository<T> {

    public CommonMySqlRepository(String prefix, Class<T> entityClass) {
        super(
                DbConfigLoader.DB_NAME_ACCOUNT,
                prefix,
                entityClass
        );
    }

}
