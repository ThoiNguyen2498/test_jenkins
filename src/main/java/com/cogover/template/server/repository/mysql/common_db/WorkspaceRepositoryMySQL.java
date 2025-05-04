package com.cogover.template.server.repository.mysql.common_db;

import com.cogover.cache.CacheUtil;
import com.cogover.exception.DbException;
import com.cogover.template.server.config.DbConfigLoader;
import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.database.entity.common_mysql.Workspace;
import com.cogover.template.server.repository.mysql.base.CommonMySqlRepository;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/*
 *
 * @author huydn on 08/04/2024 00:31
 */
@Log4j2
public class WorkspaceRepositoryMySQL extends CommonMySqlRepository<Account> implements WorkspaceRepository {

    public static final int CACHE_TIMEOUT_SECONDS = 60 * 60;

    public WorkspaceRepositoryMySQL() {
        super("WS", Account.class);
    }

    @Override
    public Workspace getWorkspaceById(String id) throws DbException {
        return (Workspace) CacheUtil.getFromCacheOrDb(
                Workspace.TABLE_NAME,
                id,
                () -> this.findOneById(DbConfigLoader.MYSQL_COMMON, id),
                CACHE_TIMEOUT_SECONDS
        );
    }

    @Override
    public Workspace getWorkspaceByDomain(String domain) throws DbException {
        return (Workspace) CacheUtil.getFromCacheOrDb(
                Workspace.TABLE_NAME,
                domain,
                () -> this.findOne(
                        DbConfigLoader.MYSQL_COMMON,
                        "FROM Workspace where domain = :domain",
                        Map.of(
                                "domain", domain
                        )
                ),
                CACHE_TIMEOUT_SECONDS,
                null,
                true
        );
    }

}
