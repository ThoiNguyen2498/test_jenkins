package sample.rpc_via_tcp;

import com.cogover.rpc.client.RpcClient;
import com.cogover.rpc.client.impl.tcp.RpcTcpTransportClient;
import com.cogover.template.server.config.Config;
import com.cogover.template.server.controller.login.LoginRequest;
import lombok.extern.log4j.Log4j2;

/**
 * @author huydn on 05/04/2024 22:36
 */
@Log4j2
public class RpcViaTcpTest {

    public static void main(String[] args) {
        Config.initLog4j();

        RpcClient rpcClient = new RpcClient("tcp://127.0.0.1:7180", RpcTcpTransportClient.class);
        rpcClient.connect();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("huy@stringee.com");
        loginRequest.setPassword("");

        String result = (String) rpcClient.call(
                1,
                "client-1",
                loginRequest,
                2000,
                "key-1"
        );

        log.info("result: {}", result);
    }

}
