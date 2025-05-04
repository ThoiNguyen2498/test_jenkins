package com.cogover.template.server.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author huydn on 06/04/2024 12:06
 */
class ConfigTest {

    private static final Logger log = LogManager.getLogger("ConfigTest");

    /*
     * Muc dich la check sample config co dung khong; load tu config len App co dung ko
     */
    @Test
    void loadConfigSuccessfully() {
        Config.loadConfig();

        assert Config.serverConfig.getRpcTcpServerPrivate().getPort() >= 7000;
        assert Config.serverConfig.getRpcTcpServerPrivate().getPort() <= 7999;

        assert Config.serverConfig.getRpcHttpServerPrivate().getPort() >= 7000;
        assert Config.serverConfig.getRpcHttpServerPrivate().getPort() <= 7999;

        assert Config.serverConfig.getRpcHttpServerPublic().getPort() >= 10000;
        assert Config.serverConfig.getRpcHttpServerPublic().getPort() <= 10999;

        assert !Config.serverConfig.getWhitelistContinueUrlDomains().isEmpty();

        log.info("Contains domain cogover.com: {}", Config.serverConfig.getKafka().dbDataChangeTopic);

        assertTrue(Config.serverConfig.getMongodb().getPort() >= 0);
    }
}
