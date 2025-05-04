package com.cogover.template.server.controller.workspace_email;

import com.cogover.exception.DbException;
import com.cogover.rpc.server.packet.request.RpcRequest;
import com.cogover.rpc.server.packet.response.RpcResponse;
import com.cogover.template.server.network.rpc_server.handler.Controller;
import com.cogover.template.server.repository.mysql.workspace_db.WorkspaceEmailRepository;
import com.cogover.template.server.service.workspace_email.GetWorkspaceEmailService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author huydn on 21/4/25 14:55
 */
public class GetWorkspaceEmailController extends Controller<GetWorkspaceEmailRequest> {

    private GetWorkspaceEmailService service;

    public GetWorkspaceEmailController() {
        super(GetWorkspaceEmailRequest.class);
    }

    @Override
    public void init() {
        service = new GetWorkspaceEmailService(new WorkspaceEmailRepository());
    }

    @Override
    public Object process(RpcRequest rpcRequest, RpcResponse rpcResponse) throws JsonProcessingException, DbException {
        GetWorkspaceEmailRequest request = this.parseRequest(rpcRequest);
        return service.process(request);
    }
}
