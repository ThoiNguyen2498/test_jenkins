package com.cogover.template.server.network.rpc_server.server;

import com.cogover.rpc.server.impl.http_javalin.JavalinHttpRpcNetworkServer;
import com.cogover.template.server.network.rpc_server.handler.RpcHandler;
import com.cogover.rpc.server.RpcServer;
import com.cogover.rpc.server.impl.tcp.TcpRpcNetworkServer;
import com.cogover.template.server.config.Config;
import io.netty.util.AttributeKey;
import lombok.extern.log4j.Log4j2;

/**
 * @author huydn on 01/04/2024 20:19
 */
@Log4j2
public class RpcServerFactory {

    public static final AttributeKey<Boolean> FOR_PUBLIC_CLIENT = AttributeKey.valueOf("FOR_PUBLIC_CLIENT");

    private RpcServerFactory() {
    }

    public static void startAllServers() throws Exception {
        RpcHandler rpcHandler = new RpcHandler();

        viaTcp(
                Config.serverConfig.getRpcTcpServerPrivate().getHost(),
                Config.serverConfig.getRpcTcpServerPrivate().getPort(),
                rpcHandler
        );

        viaHttp(
                Config.serverConfig.getRpcHttpServerPrivate().getHost(),
                Config.serverConfig.getRpcHttpServerPrivate().getPort(),
                false,
                rpcHandler
        );

        viaHttp(
                Config.serverConfig.getRpcHttpServerPublic().getHost(),
                Config.serverConfig.getRpcHttpServerPublic().getPort(),
                true,
                rpcHandler
        );
    }

    public static void viaTcp(String host, int port, RpcHandler rpcHandler) throws Exception {
        RpcServer rpcServer = new RpcServer(host, port, TcpRpcNetworkServer.class, 10);
        rpcServer.setRpcServerHandler(rpcHandler);
        rpcServer.setAttribute(FOR_PUBLIC_CLIENT, false);
        rpcServer.start();

        log.info("RpcServer (TCP) is started: host={}, port={}", host, port);
    }

    /**
     * @param isPublic true public ra internet cho phép client kết nối vao
     */
    public static void viaHttp(String host, int port, boolean isPublic, RpcHandler rpcHandler) throws Exception {
        RpcServer rpcServer = new RpcServer(host, port, JavalinHttpRpcNetworkServer.class, 10);
        JavalinHttpRpcNetworkServer networkServer = (JavalinHttpRpcNetworkServer) rpcServer.getNetworkServer();
        networkServer.setCorsAllowedOrigins(Config.serverConfig.getCorsAllowedOrigins());

        rpcServer.setRpcServerHandler(rpcHandler);
        rpcServer.setAttribute(FOR_PUBLIC_CLIENT, isPublic);
        rpcServer.start();

        log.info("RpcServer (Via HTTP) is started: host={}, port={}, isPublic={}", host, port, isPublic);
    }

}
