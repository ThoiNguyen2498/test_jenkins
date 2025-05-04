package sample.vault;

import com.cogover.template.server.config.Config;
import com.cogover.template.server.config.DbConfigLoader;
import lombok.extern.log4j.Log4j2;

/**
 * @author huydn on 19/5/24 16:34
 */
@Log4j2
public class TestReadEnv_Dev {

    public static void main(String[] args) {
        Config.loadConfig();
        DbConfigLoader.loadDbConfig();
    }

}
