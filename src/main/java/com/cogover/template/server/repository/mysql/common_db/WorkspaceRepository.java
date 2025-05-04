package com.cogover.template.server.repository.mysql.common_db;

import com.cogover.exception.DbException;
import com.cogover.template.server.database.entity.common_mysql.Workspace;

/**
 * @author huydn on 08/04/2024 00:30
 */
public interface WorkspaceRepository {

    Workspace getWorkspaceById(String id) throws DbException;

    Workspace getWorkspaceByDomain(String domain) throws DbException;

}
