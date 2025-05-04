package com.cogover.template.server.database.entity.common_mysql;

import com.cogover.cache.CacheableData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.json.JSONObject;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account implements CacheableData {

    public static final int STATUS_REGISTERED_BUT_NOT_VERIFIED_EMAIL = 0;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_REQUEST_TO_DELETE_ACCOUNT = 2;
    public static final int STATUS_BLOCKED = -2;
    public static final int STATUS_PERMANENTLY_DELETED = -3;

    public static final String TABLE_NAME = "account";

    @Id
    @Column(name = "id", nullable = false, length = 12)
    private String id;

    @Column(name = "email", nullable = false, length = 256)
    private String email;

    @ColumnDefault("0")
    @Column(name = "status", nullable = false)
    private Byte status;

    /**
     * Check if the account is active
     */
    public boolean isActiveAndCanLogin() {
        return status == STATUS_REGISTERED_BUT_NOT_VERIFIED_EMAIL || status == STATUS_ACTIVE;
    }

    public boolean isWaitingForDeleted() {
        return status == STATUS_REQUEST_TO_DELETE_ACCOUNT;
    }

    public boolean isBlocked() {
        return status == STATUS_BLOCKED;
    }

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "password_salt", nullable = false, length = 10)
    private String passwordSalt;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_country_code", length = 5)
    private String phoneCountryCode;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "created", nullable = false)
    private Long created;

    @Column(name = "updated", nullable = false)
    private Long updated;

    @Lob
    @Column(name = "utm_params")
    private String utmParams;

    @Column(name = "register_ip", nullable = false, length = 64)
    private String registerIp;

    @Lob
    @Column(name = "avatar")
    private String avatar;

    @Column(name = "language", nullable = false, length = 10)
    private String language;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone;

    @Lob
    @Column(name = "setting")
    private String setting;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "birthday")
    private Integer birthday;

    @Column(name = "email_register", length = 256)
    private String emailRegister;

    @Column(name = "date_format", length = 50)
    private String dateFormat;

    @Column(name = "time_format", length = 50)
    private String timeFormat;

    @Column(name = "enable_2fa")
    private boolean enable2fa;

    @Column(name = "number_format", length = 50)
    private String numberFormat;


    public JSONObject toPublicJson(boolean forPrivateClient) {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("email", email);
        json.put("status", status);
        json.put("firstName", firstName);
        json.put("lastName", lastName);
        json.put("created", created);
        json.put("updated", updated);
        json.put("phoneNumber", phoneNumber);
        json.put("phoneCountryCode", phoneCountryCode);
        json.put("created", created);
        json.put("updated", updated);
        if (forPrivateClient) {
            json.put("registerIp", registerIp);
        }
        if (forPrivateClient) {
            json.put("avatar", avatar);
        }
        json.put("language", language);
        json.put("timezone", timezone);
        json.put("setting", setting);
        json.put("address", address);
        json.put("birthday", birthday);
        json.put("dateFormat", dateFormat);
        json.put("numberFormat", numberFormat);
        json.put("timeFormat", timeFormat);
        json.put("enable2fa", enable2fa);
        json.put("hasPassword", password != null);
        return json;
    }

    @Override
    public List<String> getTags() {
        return List.of(id);
    }
}
