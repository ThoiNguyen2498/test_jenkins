package com.cogover.template.server.network.rpc_server.handler.interceptor;

import com.cogover.rpc.server.handler.HandlerInterceptor;
import com.cogover.rpc.server.packet.request.RpcRequest;
import com.cogover.rpc.server.packet.response.RpcResponse;
import lombok.extern.log4j.Log4j2;

/**
 * @author huydn on 26/4/24 17:29
 */
@Log4j2
public class HttpSession2Handler implements HandlerInterceptor {

    @Override
    public boolean preHandle(RpcRequest request, RpcResponse response) {
        log.info("++222 preHandle HttpSession2Handler is Calling");
        return true;
    }

    @Override
    public void postHandle(RpcRequest request, RpcResponse response) {
        log.info("++222 postHandle HttpSession2Handler is Calling");
    }

}
