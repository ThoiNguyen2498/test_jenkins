package com.cogover.template.server.network.rpc_server.handler;

import com.cogover.template.server.controller.login.LoginController;
import com.cogover.template.server.controller.workspace_email.GetWorkspaceEmailController;
import com.cogover.template.server.exception.ServiceException;
import com.cogover.template.server.network.rpc_server.handler.interceptor.HttpSession2Handler;
import com.cogover.template.server.network.rpc_server.handler.interceptor.TracerInterceptor;
import com.cogover.template.server.network.rpc_server.handler.interceptor.HttpSessionHandler;
import com.cogover.template.server.network.rpc_server.server.ServiceType;
import com.cogover.rpc.server.handler.RpcServerHandler;
import com.cogover.rpc.server.packet.request.RpcRequest;
import com.cogover.rpc.server.packet.response.RpcResponse;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huydn on 01/04/2024 20:44
 */
@Log4j2
public class RpcHandler extends RpcServerHandler {

    private static final Map<Integer, Controller<?>> CONTROLLERS = new HashMap<>();

    static {
        CONTROLLERS.put(ServiceType.SERVICE_LOGIN, new LoginController());
        CONTROLLERS.put(ServiceType.SERVICE_GET_WORKSPACE_EMAIL, new GetWorkspaceEmailController());
    }

    /**
     * Interceptor được add vào đầu tiên thì hàm "preHandle" sẽ chạy đầu tiên;
     * Interceptor được add vào đầu tiên thì hàm "postHandle" sẽ chạy cuối cùng
     */

    public RpcHandler() {
        //interceptors
        interceptors.add(new TracerInterceptor());
        interceptors.add(new HttpSessionHandler());
        interceptors.add(new HttpSession2Handler());

        //init
        CONTROLLERS.values().forEach(Controller::init);
    }

    private String error(int r, String msg) {
        JSONObject obj = new JSONObject();
        obj.put("r", r);
        obj.put("msg", msg);
        return obj.toString();
    }

    @Override
    public Object process(RpcRequest request, RpcResponse response) {
        log.info("process RPC request: service={}, data={}", request.getService(), request.getBody());

        Controller<?> controller = CONTROLLERS.get(request.getService());
        if (controller == null) {
            log.info("Can not found processor for request: service={}, data={}", request.getService(), request.getBody());
            return error(5001, "Can not found processor for request: service=" + request.getService());
        }

        try {
            long start = System.currentTimeMillis();
            Object res = controller.process(request, response);
            long processTime = System.currentTimeMillis() - start;
            log.info("Send response: service={}, res={}, processTime={} ms", request.getService(), res, processTime);
            return res;
        } catch (ServiceException e) {
            return error(e.getR(), e.getMsg());
        } catch (Exception | Error e) {
            log.error(e, e);
            return error(5000, "Error");
        }
    }

}
