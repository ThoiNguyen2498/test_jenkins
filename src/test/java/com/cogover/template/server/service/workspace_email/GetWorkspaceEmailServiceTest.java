package com.cogover.template.server.service.workspace_email;

import com.cogover.exception.DbException;
import com.cogover.template.server.common.ResponseDTO;
import com.cogover.template.server.config.Config;
import com.cogover.template.server.controller.workspace_email.EmailWorkspaceDTO;
import com.cogover.template.server.controller.workspace_email.GetWorkspaceEmailRequest;
import com.cogover.template.server.controller.workspace_email.GetWorkspaceEmailResponse;
import com.cogover.template.server.database.entity.workspace_mysql.EmailWorkspace;
import com.cogover.template.server.repository.mysql.workspace_db.WorkspaceEmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author huydn on 22/4/25 00:53
 */
class GetWorkspaceEmailServiceTest {

    @Mock
    private WorkspaceEmailRepository workspaceEmailRepository;

    @InjectMocks
    private GetWorkspaceEmailService getWorkspaceEmailService;

    @BeforeEach
    void setUp() {
        Config.initLog4j();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processReturnsResponseWithEmailWorkspacesWhenValidRequest() throws DbException {
        GetWorkspaceEmailRequest request = new GetWorkspaceEmailRequest("WSrkrdKHx7bje", "MYSQL_APPLE");

        EmailWorkspace emailWorkspace = new EmailWorkspace();
        emailWorkspace.setEmail("huy@stringee.com");
        List<EmailWorkspace> emailWorkspaces = List.of(emailWorkspace);

        when(workspaceEmailRepository.findAllByWorkspaceId("MYSQL_APPLE", "WSrkrdKHx7bje")).thenReturn(emailWorkspaces);

        ResponseDTO<GetWorkspaceEmailResponse> response = getWorkspaceEmailService.process(request);

        assertEquals(0, response.getR());
        assertEquals("OK", response.getMsg());
        assertEquals("WSrkrdKHx7bje", response.getData().getWorkspaceId());

        EmailWorkspaceDTO responseEmailWorkspace = response.getData().getEmailWorkspaces().getFirst();

        assertEquals("huy@stringee.com", responseEmailWorkspace.getEmail());
    }

    @Test
    void processReturnsEmptyResponseWhenNoEmailWorkspacesFound() throws DbException {
        GetWorkspaceEmailRequest request = new GetWorkspaceEmailRequest("WSrkrdKHx7bje", "MYSQL_APPLE");

        when(workspaceEmailRepository.findAllByWorkspaceId("MYSQL_APPLE", "WSrkrdKHx7bje")).thenReturn(List.of());

        ResponseDTO<GetWorkspaceEmailResponse> response = getWorkspaceEmailService.process(request);

        assertEquals(0, response.getR());
        assertEquals("OK", response.getMsg());
        assertEquals("WSrkrdKHx7bje", response.getData().getWorkspaceId());
        assertTrue(response.getData().getEmailWorkspaces().isEmpty());
    }
}
