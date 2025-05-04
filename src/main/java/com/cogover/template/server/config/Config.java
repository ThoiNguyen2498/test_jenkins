package com.cogover.template.server.config;

import com.cogover.common.Pair;
import com.cogover.template.server.network.cluster.AuthorizationServer;
import com.cogover.vault.VaultFactory;
import com.cogover.vault.utils.VaultUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.Configurator;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

@Log4j2
public class Config {

    private static final String APP_CONF_DIR = System.getProperty("user.dir") + File.separator + "config";
    private static final String APP_CONF_FILE_PATH = APP_CONF_DIR + File.separator + "app.yaml";
    private static final String LOG_CONF_FILE_PATH = APP_CONF_DIR + File.separator + "log4j2.xml";

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public static ServerConfig serverConfig;
    public static final String NODE_ID = "template-server-" + UUID.randomUUID();

    private Config() {
    }

    public static void initLog4j() {
        try {
            Configurator.initialize(null, LOG_CONF_FILE_PATH);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void loadConfig() {
        try {
            initLog4j();

            serverConfig = MAPPER.readValue(new File(APP_CONF_FILE_PATH), ServerConfig.class);

            //overwrite config from Vault
            readConfigFromVault();
        } catch (Exception ex) {
            log.error("Error when load config file from: {}", APP_CONF_DIR, ex);
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
            }
        }
    }

    private static void readConfigFromVault() {
        VaultFactory.getInstance().setUp();

        rpcHttpServerPrivateConfig();
        kafkaConfig();
        mongoConfig();
        authorizationServer();
    }

    private static void rpcHttpServerPrivateConfig() {
        Pair<String, String> rpcHttpServerPrivateUp = VaultUtil.settingVaultDbCredentials(
                Config.serverConfig.getVault().getRpcHttpServerPrivatePath()
        );
        log.info("Vault: rpcHttpServerPrivate username: {}, password: ***", rpcHttpServerPrivateUp.getFirst());
        Config.serverConfig.rpcHttpServerPrivate.setUsername(rpcHttpServerPrivateUp.getFirst());
        Config.serverConfig.rpcHttpServerPrivate.setPassword(rpcHttpServerPrivateUp.getSecond());
    }

    private static void kafkaConfig() {
        JSONObject json = VaultUtil.getModuleAddr("kafka");
        Config.serverConfig.kafka.setUri(json.getString("rw"));
        Config.serverConfig.kafka.setUsername(json.getString("username"));
        Config.serverConfig.kafka.setPassword(json.getString("password"));
    }

    private static void mongoConfig() {
        JSONObject json = VaultUtil.getModuleAddr("mongo-common");
        Config.serverConfig.mongodb.setConnectionString(json.getString("connection_string"));
    }

    private static void authorizationServer() {
        JSONObject json = VaultUtil.getModuleAddr("auth-server-rpc");
        String[] rpcViaTcpHostPort = json.getString("tcp").split(":");
        Config.serverConfig.authorizationServer.setHost(
                rpcViaTcpHostPort[0]
        );
        Config.serverConfig.authorizationServer.setPort(
                Integer.parseInt(rpcViaTcpHostPort[1])
        );
        log.info("Vault: authorizationServerAddr: tcp://{}:{}", Config.serverConfig.authorizationServer.getHost(), Config.serverConfig.authorizationServer.getPort());

        AuthorizationServer.initRpcClient();
    }

}
