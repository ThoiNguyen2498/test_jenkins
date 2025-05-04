package com.cogover.template.server.controller.workspace_email;

import lombok.*;

/**
 * DTO for EmailWorkspace entity
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailWorkspaceDTO {

    private String id;
    //private String workspaceId;
    private Byte type;
    private String email;
    private String displayName;
    private String smtpHost;
    private Integer smtpPort;
    private Byte status;
    private Long created;
    private Long updated;
}
