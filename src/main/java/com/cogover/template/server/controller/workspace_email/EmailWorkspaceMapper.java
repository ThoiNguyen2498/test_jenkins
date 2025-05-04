package com.cogover.template.server.controller.workspace_email;

import com.cogover.template.server.database.entity.workspace_mysql.EmailWorkspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EmailWorkspaceMapper {

    EmailWorkspaceMapper INSTANCE = Mappers.getMapper(EmailWorkspaceMapper.class);

    /**
     * Mặc định là map hết, không muốn thì bỏ ra
     */
    //@Mapping(target = "workspaceId", ignore = true)
    EmailWorkspaceDTO toDto(EmailWorkspace entity);

    List<EmailWorkspaceDTO> toDtoList(List<EmailWorkspace> entities);
}
