package com.cogover.template.server.repository.common;

import com.cogover.exception.DbException;

/**
 * @author huydn on 2/8/24 15:26
 */
public interface DbRepository<T> {

    void insert(String mysqlId, T object) throws DbException;
    T findOneByWorkspaceAndId(String mysqlId, String workspaceId, String id) throws DbException;
    T findOneById(String mysqlId, String id) throws DbException;
    void update(String mysqlId, T object) throws DbException;
    void delete(String mysqlId, T object) throws DbException;

}
