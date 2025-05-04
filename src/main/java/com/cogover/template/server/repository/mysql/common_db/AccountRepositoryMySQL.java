package com.cogover.template.server.repository.mysql.common_db;

import com.cogover.cache.CacheUtil;
import com.cogover.exception.DbException;
import com.cogover.template.server.Start;
import com.cogover.template.server.config.DbConfigLoader;
import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.network.pub_sub.db_data_change.DatabaseDataChange;
import com.cogover.template.server.network.pub_sub.publisher.Producer;
import com.cogover.template.server.repository.mysql.base.CommonMySqlRepository;
import io.opentelemetry.api.trace.Span;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.util.Map;

/*
 *
 * @author huydn on 08/04/2024 00:31
 */
@Log4j2
public class AccountRepositoryMySQL extends CommonMySqlRepository<Account> implements AccountRepository {

    public static final int CACHE_TIMEOUT_SECONDS = 60 * 60;

    public AccountRepositoryMySQL() {
        super("AC", Account.class);
    }

    @Override
    public Account getAccountByEmail(String email, boolean ignoreDeletedAndSpamAccount) throws DbException {
        //test: tao 1 span de do thoi gian goi DB
        Span spanDb = Start.tracer.spanBuilder("getAccountByEmail").startSpan();
        spanDb.makeCurrent();
        try {
            String uniqueValue = email + "_ignoreDeletedAndSpamAccount-" + ignoreDeletedAndSpamAccount;
            return (Account) CacheUtil.getFromCacheOrDb(
                    Account.TABLE_NAME,
                    uniqueValue,
                    () -> {
                        String sql;
                        if (ignoreDeletedAndSpamAccount) {
                            //-1: Email bị đánh dấu là của người khác
                            //-3: Đã xóa hoàn toàn
                            sql = "FROM Account where email = :email and status <> -1 and status <> -3";
                        } else {
                            sql = "FROM Account where email = :email";
                        }

                        return this.findOne(DbConfigLoader.MYSQL_COMMON, sql, Map.of("email", email));
                    },
                    CACHE_TIMEOUT_SECONDS,
                    null,
                    true
            );
        } finally {
            spanDb.end();
        }
    }

    @Override
    public Account getAccountById(String id) throws DbException {
        return (Account) CacheUtil.getFromCacheOrDb(
                Account.TABLE_NAME,
                id,
                () -> this.findOneById(DbConfigLoader.MYSQL_COMMON, id),
                CACHE_TIMEOUT_SECONDS,
                null,
                false
        );
    }


    @Override
    public int updateAccountStatus(String accountId, int status) throws DbException {
        int c = this.update(
                DbConfigLoader.MYSQL_COMMON,
                "UPDATE Account SET status=:status, updated=:updated WHERE id = :id",
                Map.of(
                        "status", status,
                        "id", accountId,
                        "updated", System.currentTimeMillis()
                )
        );

        if (c > 0) {
            this.deleteCacheAndPublishChange(accountId, status);
        }

        return c;
    }

    private void deleteCacheAndPublishChange(String accountId, int status) {
        //xoa cache luon (vi cho Kafka se bi cham)
        CacheUtil.delete(Account.TABLE_NAME, accountId);

        //gui thong bao den Kafka de xoa cache tren cac node khac
        JSONObject data = new JSONObject();
        data.put("id", accountId);
        data.put("status", status);
        Producer.sendDbDataChange(
                DatabaseDataChange.Database.MYSQL,
                Account.TABLE_NAME,
                DatabaseDataChange.Type.UPDATE,
                data
        );
    }

}
