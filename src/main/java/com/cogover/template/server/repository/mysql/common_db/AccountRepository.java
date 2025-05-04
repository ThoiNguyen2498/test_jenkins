package com.cogover.template.server.repository.mysql.common_db;

import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.exception.DbException;

/**
 * @author huydn on 08/04/2024 00:30
 */
public interface AccountRepository {

    Account getAccountByEmail(String email, boolean ignoreDeletedAndSpamAccount) throws DbException;

    Account getAccountById(String id) throws DbException;

    int updateAccountStatus(String accountId, int status) throws DbException;

}
