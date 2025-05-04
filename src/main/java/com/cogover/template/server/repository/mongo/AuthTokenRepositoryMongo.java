package com.cogover.template.server.repository.mongo;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import com.cogover.template.server.database.entity.common_mysql.Workspace;
import com.cogover.template.server.database.util.mongo.MongoUtil;
import com.cogover.template.server.network.pub_sub.db_data_change.DatabaseDataChange;
import com.cogover.template.server.network.pub_sub.publisher.Producer;
import com.cogover.template.server.util.ServerUtil;
import com.cogover.cache.CacheUtil;
import com.cogover.exception.DbException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import static com.cogover.template.server.repository.mysql.common_db.WorkspaceRepositoryMySQL.CACHE_TIMEOUT_SECONDS;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @author huydn on 09/04/2024 14:06
 */
@Log4j2
public class AuthTokenRepositoryMongo implements AuthTokenRepository {

    private final MongoCollection<AuthToken> collection;

    public AuthTokenRepositoryMongo() {
        MongoDatabase mongoDB = MongoUtil.getDatabase(Config.serverConfig.getMongodb());
        collection = mongoDB.getCollection(AuthToken.COLLECTION_NAME, AuthToken.class);
        createIndexes();
    }

    private void createIndexes() {
        collection.createIndex(Indexes.ascending("workspace_id"));
        collection.createIndex(Indexes.ascending("parent_id"));
        collection.createIndex(Indexes.ascending("account_id"));
    }

    private AuthToken findOne(Bson filter) {
        return collection.find(filter).first();
    }

    @Override
    public AuthToken findOne(ObjectId parentId, String workspaceId) throws DbException {
        String uValue = parentId.toHexString() + "_" + workspaceId;

        return (AuthToken) CacheUtil.getFromCacheOrDb(
                AuthToken.COLLECTION_NAME,
                uValue,
                () -> findOne(
                        Filters.and(
                                Filters.eq("parent_id", parentId),
                                Filters.eq("workspace_id", workspaceId)
                        )
                ),
                CACHE_TIMEOUT_SECONDS,
                null,
                true
        );
    }

    @Override
    public AuthToken findOne(String hexObjectId) throws DbException {
        return (AuthToken) CacheUtil.getFromCacheOrDb(
                AuthToken.COLLECTION_NAME,
                hexObjectId,
                () -> {
                    Bson filter = eq("_id", new ObjectId(hexObjectId));
                    return findOne(filter);
                },
                CACHE_TIMEOUT_SECONDS,
                null,
                true
        );
    }

    @Override
    public long deleteTokenAndChildren(ObjectId objectId) {
        long c1 = delete(eq("_id", objectId));
        long c2 = delete(eq("parent_id", objectId));
        long c = c1 + c2;

        if (c > 0) {
            CacheUtil.delete(AuthToken.COLLECTION_NAME, objectId.toHexString());
            CacheUtil.deleteByTag(AuthToken.COLLECTION_NAME + ":parent_id:" + objectId.toHexString());

            //gui thong bao den Kafka de xoa cache tren cac node khac
            JSONObject data = new JSONObject();
            data.put("id", objectId.toHexString());
            Producer.sendDbDataChange(
                    DatabaseDataChange.Database.MONGO,
                    AuthToken.COLLECTION_NAME,
                    DatabaseDataChange.Type.DELETE,
                    data
            );
        }
        return c;
    }

    private long delete(Bson condition) {
        long ret = 0L;
        try {
            DeleteResult result = collection.deleteMany(condition);
            ret = result.getDeletedCount();
        } catch (Exception ex) {
            log.error("Exception: ", ex);
        }
        return ret;
    }

    /**
     * Update OTP verify expired time; cập nhật cần xoá cache có tag là "accountId"
     */
    @Override
    public long updateOtpVerifyExpiredTime(String accountId, String deviceId, int otpVerifyExpiredTime) {
        Bson condition = and(
                eq("account_id", accountId),
                eq("device_id", deviceId)
        );
        Bson updates = Updates.set("otp_verify_expired_time", otpVerifyExpiredTime);
        UpdateResult updateResult = collection.updateMany(
                condition,
                updates
        );
        long c = updateResult.getModifiedCount();
        if (c > 0) {
            CacheUtil.deleteByTag(AuthToken.COLLECTION_NAME + ":account_id:" + accountId);

            //gui thong bao den Kafka de xoa cache tren cac node khac
            JSONObject data = new JSONObject();
            data.put("id", "");
            data.put("account_id", accountId);
            data.put("device_id", deviceId);
            Producer.sendDbDataChange(
                    DatabaseDataChange.Database.MONGO,
                    AuthToken.COLLECTION_NAME,
                    DatabaseDataChange.Type.UPDATE,
                    data
            );
        }
        return c;
    }

    @Override
    public AuthToken generateNewAccountToken(Account account, String deviceId, JSONObject deviceInfo, String clientIp) {
        return generateNewToken(account.getId(), null, deviceId, deviceInfo, null, clientIp);
    }

    @Override
    public AuthToken generateNewWorkspaceToken(Account account, Workspace workspace, String deviceId, JSONObject deviceInfo, AuthToken accountToken, String clientIp) {
        return generateNewToken(account.getId(), workspace.getId(), deviceId, deviceInfo, accountToken, clientIp);
    }

    private AuthToken generateNewToken(String accountId, String workspaceId, String deviceId, JSONObject deviceInfo, AuthToken accountToken, String clientIp) {
        long created = System.currentTimeMillis();
        String secretKey = ServerUtil.randomString(24);
        int expiredTime = (int) (created / 1000) + 30 * 86400;//30 days
        int refreshTimeout = (int) (created / 1000) + 10 * 60;//10 minutes

        String clientType = deviceInfo.optString("clientType", "web-app");
        long lastAccessTime = System.currentTimeMillis();
        String os = deviceInfo.optString("os", "");
        String userAgent = deviceInfo.optString("userAgent", "");
        String clientAppVersion = deviceInfo.optString("clientAppVersion", "");

        AuthToken authToken = AuthToken.builder()
                .type(workspaceId != null ? 2 : 1)//1: Account/ID; 2: Workspace
                .parentId(accountToken != null ? accountToken.getId() : null)
                .secretKey(secretKey)
                .expiredTime(expiredTime)
                .refreshTimeout(refreshTimeout)
                .accountId(accountId)
                .workspaceId(workspaceId)
                .created(created)
                .updated(created)
                .clientType(clientType)
                .clientAppVersion(clientAppVersion)
                .lastAccessTime(lastAccessTime)
                .lastClientIp(clientIp)
                .os(os)
                .userAgent(userAgent)
                .deviceId(deviceId)
                .otpVerifyExpiredTime(accountToken == null ? 0 : accountToken.getOtpVerifyExpiredTime())
                .build();

        //save Mongo
        collection.insertOne(authToken);

        return authToken;
    }

}
