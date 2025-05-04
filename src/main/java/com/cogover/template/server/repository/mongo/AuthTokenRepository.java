package com.cogover.template.server.repository.mongo;

import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import com.cogover.template.server.database.entity.common_mysql.Workspace;
import com.cogover.exception.DbException;
import org.bson.types.ObjectId;
import org.json.JSONObject;

/**
 * @author huydn on 09/04/2024 14:02
 */
public interface AuthTokenRepository {

    long deleteTokenAndChildren(ObjectId objectId);

    AuthToken findOne(ObjectId parentId, String workspaceId) throws DbException;

    AuthToken findOne(String hexObjectId) throws DbException;

    long updateOtpVerifyExpiredTime(String accountId, String deviceId, int otpVerifyExpiredTime);

    AuthToken generateNewAccountToken(Account account, String deviceId, JSONObject deviceInfo, String clientIp);

    AuthToken generateNewWorkspaceToken(Account account, Workspace workspace, String deviceId, JSONObject deviceInfo, AuthToken accountToken, String clientIp);

}
