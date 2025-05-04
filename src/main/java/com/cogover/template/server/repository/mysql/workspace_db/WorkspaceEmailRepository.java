package com.cogover.template.server.repository.mysql.workspace_db;

import com.cogover.template.server.database.entity.workspace_mysql.EmailWorkspace;
import com.cogover.template.server.repository.mysql.base.WorkspaceMySqlRepository;

/**
 * @author huydn on 30/7/24 12:15
 */
public class WorkspaceEmailRepository extends WorkspaceMySqlRepository<EmailWorkspace> {

    public WorkspaceEmailRepository() {
        super("EW", EmailWorkspace.class);
    }

}
