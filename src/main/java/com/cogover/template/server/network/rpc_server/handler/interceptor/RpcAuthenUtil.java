package com.cogover.template.server.network.rpc_server.handler.interceptor;

import com.cogover.template.server.util.HttpUtil;
import com.cogover.template.server.config.Config;
import com.cogover.rpc.server.packet.request.HttpRpcRequest;
import com.cogover.rpc.server.packet.request.RpcRequest;

/**
 * @author huydn on 17/4/24 13:58
 */
public class RpcAuthenUtil {

    private RpcAuthenUtil() {
    }

    public static boolean isAuthenticated(RpcRequest request) {
        boolean ok = false;

        if (request instanceof HttpRpcRequest httpRpcRequest) {
            //neu la request den HTTP interface
            String[] usernamePassword = HttpUtil.decodeAuthorizationHeader(httpRpcRequest.getHeaders().get("Authorization"));
            if (usernamePassword != null && usernamePassword.length == 2) {
                String rpcUsername = usernamePassword[0];
                String rpcPassword = usernamePassword[1];
                if (
                        rpcUsername.equals(Config.serverConfig.getRpcHttpServerPrivate().getUsername()) &&
                                rpcPassword.equals(Config.serverConfig.getRpcHttpServerPrivate().getPassword())
                ) {
                    ok = true;
                }
            }
        } else {
            //neu den tu TCP, vi da xac thuc o buoc khoi tao connection roi
            ok = true;
        }

        return ok;
    }

}
