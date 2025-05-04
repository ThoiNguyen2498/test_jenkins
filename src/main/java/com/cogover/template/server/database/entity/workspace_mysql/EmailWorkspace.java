package com.cogover.template.server.database.entity.workspace_mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "email_workspace")
public class EmailWorkspace {
    @Id
    @Column(name = "id", nullable = true, length = 15)
    private String id;

    @Column(name = "workspace_id", nullable = true, length = 15)
    private String workspaceId;

    @Column(name = "type", nullable = true)
    private Byte type;

    @Column(name = "email", nullable = true)
    private String email;

    @ColumnDefault("''")
    @Column(name = "display_name", nullable = true)
    private String displayName;

    @Lob
    @Column(name = "smtp_password")
    private String smtpPassword;

    @ColumnDefault("''")
    @Column(name = "smtp_host", nullable = true)
    private String smtpHost;

    @ColumnDefault("0")
    @Column(name = "smtp_port", nullable = true)
    private Integer smtpPort;

    @Lob
    @Column(name = "oa_token")
    private String oaToken;

    @Lob
    @Column(name = "oa_refresh_token")
    private String oaRefreshToken;

    @ColumnDefault("(0)")
    @Column(name = "oa_expired", nullable = true)
    private Long oaExpired;

    @Column(name = "sendgrid_api_key")
    private String sendgridApiKey;

    @ColumnDefault("1")
    @Column(name = "status", nullable = true)
    private Byte status;

    @Column(name = "created_by", nullable = true, length = 15)
    private String createdBy;

    @Column(name = "updated_by", nullable = true, length = 15)
    private String updatedBy;

    @ColumnDefault("0")
    @Column(name = "created", nullable = true)
    private Long created;

    @ColumnDefault("0")
    @Column(name = "updated", nullable = true)
    private Long updated;

}
