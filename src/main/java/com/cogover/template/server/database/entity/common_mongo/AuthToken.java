package com.cogover.template.server.database.entity.common_mongo;

import com.cogover.template.server.service.login.JwtToken;
import com.cogover.cache.CacheableData;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huydn on 09/04/2024 14:34
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken implements CacheableData {

    public static final String COLLECTION_NAME = "auth_token";
    public static final int TOKEN_TYPE_ACCOUNT = 1;
    public static final int TOKEN_TYPE_WORKSPACE = 2;

    private ObjectId id;

    /**
     * 1: Account/ID; 2: Workspace
     */
    private int type;

    public boolean isAccountToken() {
        return type == 1;
    }

    @BsonProperty(value = "parent_id")
    private ObjectId parentId;

    @BsonProperty(value = "account_id")
    private String accountId;

    @BsonProperty(value = "workspace_id")
    private String workspaceId;

    @BsonProperty(value = "secret_key")
    private String secretKey;

    //second
    @BsonProperty(value = "expired_time")
    private Integer expiredTime;

    //second
    @BsonProperty(value = "refresh_timeout")
    private Integer refreshTimeout;

    //ms
    @BsonProperty(value = "created")
    private Long created;

    //ms
    @BsonProperty(value = "updated")
    private Long updated;

    @BsonProperty(value = "client_type")
    private String clientType;

    @BsonProperty(value = "client_app_version")
    private String clientAppVersion;

    @BsonProperty(value = "last_client_ip")
    private String lastClientIp;

    //lan cuoi cung su dung token nay
    @BsonProperty(value = "last_access_time")
    private Long lastAccessTime;

    @BsonProperty(value = "address")
    private String address;

    @BsonProperty(value = "os")
    private String os;

    @BsonProperty(value = "browser")
    private String browser;

    @BsonProperty(value = "user_agent")
    private String userAgent;

    //sử dụng cho mục đích xác thực 2 lớp
    @BsonProperty(value = "device_id")
    private String deviceId;

    //sử dụng cho mục đích xác thực 2 lớp (second)
    @BsonProperty(value = "otp_verify_expired_time")
    private Integer otpVerifyExpiredTime;

    @Override
    public List<String> getTags() {
        List<String> list = new ArrayList<>(List.of(
                id.toHexString(),
                COLLECTION_NAME + ":account_id:" + accountId
        ));

        if (parentId != null) {
            list.add(COLLECTION_NAME + ":parent_id:" + parentId);
        }

        return list;
    }

    public String genJwt(){
        return JwtToken.generateToken(this);
    }

}
