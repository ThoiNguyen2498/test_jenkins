package com.cogover.template.server.database.entity.common_mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "db_config")
public class DbConfig {

    @Id
    @Column(name = "id", nullable = false, length = 15)
    private String id;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "vault_key", nullable = false)
    private String vaultKey;

    @ColumnDefault("1")
    @Column(name = "status", nullable = false)
    private boolean status;

}
