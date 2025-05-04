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
@Table(name = "workspace")
public class Workspace implements CacheableData {

    public static final String TABLE_NAME = "workspace";

    @Id
    @Column(name = "id", nullable = false, length = 12)
    private String id;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "domain", nullable = false, length = 64)
    private String domain;

    @Column(name = "account_id", nullable = false, length = 12)
    private String accountId;

    @Column(name = "dc_id", nullable = false, length = 20)
    private String dcId;

    @Column(name = "mysql", length = 50)
    private String mysql;

    @Column(name = "elasticsearch", length = 50)
    private String elasticsearch;

    @Column(name = "mongodb", length = 50)
    private String mongodb;

    @ColumnDefault("1")
    @Column(name = "status", nullable = false)
    private Byte status;

    @Lob
    @Column(name = "setting")
    private String setting;

    @Column(name = "created", nullable = false)
    private Long created;

    @Column(name = "updated", nullable = false)
    private Long updated;

    @Column(name = "backend_settings")
    private String backendSettings;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "ready_for_use")
    private Boolean readyForUse;

    @Column(name = "language")
    private String language;

    @Column(name = "object_mapping")
    private String objectMapping;

    @Column(name = "number_format")
    private String numberFormat;

    @Column(name = "time_format")
    private String timeFormat;

    public JSONObject toPublicJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("domain", domain);
        json.put("setting", setting);
        json.put("created", created);
        json.put("updated", updated);
        return json;
    }

    @Override
    public List<String> getTags() {
        return List.of(id);
    }
}
