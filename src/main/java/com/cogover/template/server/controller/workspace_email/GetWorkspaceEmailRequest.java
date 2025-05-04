package com.cogover.template.server.controller.workspace_email;

import com.cogover.template.server.common.RequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author huydn on 21/4/25 14:56
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetWorkspaceEmailRequest extends RequestDto {

    private String workspaceId;
    private String mysqlId;

}
