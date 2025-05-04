package com.cogover.template.server.service.workspace_email;

import com.cogover.exception.DbException;
import com.cogover.template.server.common.ResponseDTO;
import com.cogover.template.server.controller.workspace_email.GetWorkspaceEmailRequest;
import com.cogover.template.server.controller.workspace_email.GetWorkspaceEmailResponse;
import com.cogover.template.server.database.entity.workspace_mysql.EmailWorkspace;
import com.cogover.template.server.controller.workspace_email.EmailWorkspaceMapper;
import com.cogover.template.server.repository.mysql.workspace_db.WorkspaceEmailRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author huydn on 21/4/25 14:58
 */
public class GetWorkspaceEmailService {

    private final static Logger log = LogManager.getLogger("GetWorkspaceEmailService");

    private final WorkspaceEmailRepository workspaceEmailRepository;
    private final EmailWorkspaceMapper emailWorkspaceMapper = EmailWorkspaceMapper.INSTANCE;

    public GetWorkspaceEmailService(WorkspaceEmailRepository workspaceEmailRepository) {
        this.workspaceEmailRepository = workspaceEmailRepository;
    }

    /**
     * {
     * "workspaceId": "WSrkrdKHx7bje",
     * "mysqlId": "MYSQL_APPLE"
     * }
     */
    public ResponseDTO<GetWorkspaceEmailResponse> process(GetWorkspaceEmailRequest request) throws DbException {
        log.info("Processing GetWorkspaceEmailRequest: {}", request);

        List<EmailWorkspace> emailWorkspaces = workspaceEmailRepository.findAllByWorkspaceId(
                request.getMysqlId(),
                request.getWorkspaceId()
        );

        GetWorkspaceEmailResponse response = new GetWorkspaceEmailResponse();
        response.setWorkspaceId(request.getWorkspaceId());
        response.setEmailWorkspaces(
                emailWorkspaceMapper.toDtoList(emailWorkspaces)
        );

        return new ResponseDTO<>(0, "OK", response);
    }
}
