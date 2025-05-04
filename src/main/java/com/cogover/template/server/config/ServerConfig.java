package com.cogover.template.server.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/*
 * @author huydn on 01/04/2024 14:50
 */
@Getter
@Setter
public class ServerConfig {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsernamePassword {
        String username;
        String password;
    }

    @Getter
    @Setter
    public static class RpcEndpoint extends UsernamePassword {
        String host;
        int port;
    }

    @Getter
    @Setter
    public static class Endpoint extends UsernamePassword {
        String uri;
    }

    @Getter
    @Setter
    public static class Kafka extends Endpoint {
        String dbDataChangeTopic;
        boolean enableAuth;
    }

    @Getter
    @Setter
    public static class MongoDB {
        String host;
        int port;
        String database;
        String connectionString;
    }

    @Getter
    @Setter
    public static class VaultCfg {
        String rpcHttpServerPrivatePath;
    }

    VaultCfg vault;
    RpcEndpoint rpcTcpServerPrivate;
    RpcEndpoint rpcHttpServerPrivate;
    RpcEndpoint rpcHttpServerPublic;
    MongoDB mongodb;
    Kafka kafka;
    List<String> whitelistContinueUrlDomains;
    String corsAllowedOrigins;

    RpcEndpoint authorizationServer = new RpcEndpoint();
}
