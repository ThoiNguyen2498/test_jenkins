package com.cogover.template.server.network.cluster;

import com.cogover.rpc.client.RpcClient;
import com.cogover.rpc.client.impl.tcp.RpcTcpTransportClient;
import com.cogover.template.server.config.Config;
import com.cogover.template.server.network.cluster.pojo.CheckTokenResponse;
import org.json.JSONObject;

/**
 * @author huydn on 16/8/24 13:30
 */
public class AuthorizationServer {

    private static RpcClient rpcClient;
    private static final int DEFAULT_TIMEOUT_MS = 3000;

    private static final int SERVICE_CHECK_TOKEN = 2;

    public static void initRpcClient() {
        rpcClient = new RpcClient("tcp://" +
                Config.serverConfig.getAuthorizationServer().getHost() +
                ":" +
                Config.serverConfig.getAuthorizationServer().getPort(),
                RpcTcpTransportClient.class);
    }

    public static JSONObject call(int reqService, JSONObject json, int timeoutInMiliSeconds) {
        //call(int reqService, String from, Object body, int timeoutInMiliSeconds, String keyForChooseClient)
        return rpcClient.call("client-1", reqService, json, timeoutInMiliSeconds, "1");
    }

    public static CheckTokenResponse checkToken(String authToken, String csrfToken, String sessionId) {
        JSONObject request = new JSONObject();
        request.put("csrfToken", csrfToken);
        request.put("httpSessionId", sessionId);
        request.put("token", authToken);
        request.put("continueUrl", "");
        request.put("verifyCsrfToken", true);
        JSONObject res = rpcClient.call("client-1",SERVICE_CHECK_TOKEN, request, DEFAULT_TIMEOUT_MS, "1");
        return CheckTokenResponse.build(res);
    }

}
