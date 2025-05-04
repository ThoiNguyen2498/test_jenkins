package com.cogover.template.server.controller.login;

import com.cogover.exception.DbException;
import com.cogover.rpc.server.packet.request.RpcRequest;
import com.cogover.rpc.server.packet.response.RpcResponse;
import com.cogover.template.server.network.rpc_server.handler.Controller;
import com.cogover.template.server.repository.mongo.AuthTokenRepositoryMongo;
import com.cogover.template.server.repository.mysql.common_db.AccountRepositoryMySQL;
import com.cogover.template.server.service.login.LoginService;
import com.cogover.template.server.service.password_hash.PasswordHashBCryptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;

/**
 * @author huydn on 20/4/25 00:13
 */
@Log4j2
public class LoginController extends Controller<LoginRequest> {

    private LoginService loginService;

    public LoginController() {
        super(LoginRequest.class);
    }

    @Override
    public void init() {
        loginService = new LoginService(
                new AccountRepositoryMySQL(),
                new AuthTokenRepositoryMongo(),
                new PasswordHashBCryptService()
        );
    }

    @Override
    public Object process(RpcRequest rpcRequest, RpcResponse rpcResponse) throws DbException, JsonProcessingException {
        LoginRequest request = this.parseRequest(rpcRequest);
        return loginService.process(request);
    }
}
