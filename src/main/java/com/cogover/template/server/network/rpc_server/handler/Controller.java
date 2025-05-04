package com.cogover.template.server.network.rpc_server.handler;

import com.cogover.exception.DbException;
import com.cogover.template.server.common.RequestDto;
import com.cogover.template.server.exception.ServiceException;
import com.cogover.rpc.server.packet.request.RpcRequest;
import com.cogover.rpc.server.packet.response.RpcResponse;
import com.cogover.template.server.network.rpc_server.server.RpcServerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author huydn on 08/04/2024 01:27
 */
public abstract class Controller<T extends RequestDto> {

    protected static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // Đăng ký module để xử lý Java 8 date/time
        objectMapper.findAndRegisterModules();
    }

    protected Class<T> requestDtoClass;

    public Controller(Class<T> requestDtoClass) {
        this.requestDtoClass = requestDtoClass;
    }

    public T parseRequest(RpcRequest rpcRequest) throws JsonProcessingException {
        String body = rpcRequest.getBodyAsString();
        T request = objectMapper.readValue(body, requestDtoClass);

        request.setClientIp(rpcRequest.getClientIpAddress());

        boolean fromPublicClient = rpcRequest.getRpcServer().getAttribute(RpcServerFactory.FOR_PUBLIC_CLIENT);
        request.setFromPublic(fromPublicClient);

        return request;
    }

    public abstract void init();

    public abstract Object process(RpcRequest rpcRequest, RpcResponse rpcResponse)
            throws ServiceException, DbException, JsonProcessingException;

}
