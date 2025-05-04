package com.cogover.template.server.controller.workspace_email;

import lombok.*;

import java.util.List;

/**
 * @author huydn on 21/4/25 14:56
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetWorkspaceEmailResponse {

    private String workspaceId;
    private List<EmailWorkspaceDTO> emailWorkspaces;

}
