package com.cogover.template.server.service.login;

import com.cogover.rpc.server.packet.response.impl.ResponseBodyErrorCode;

/**
 * @author huydn on 15/4/24 00:20
 */
public enum LoginError implements ResponseBodyErrorCode {

    NOT_INIT(-1),
    SUCCESS(0),
    DECRYPT_PASSWORD_ERROR(1),
    CONTINUE_URL_NOT_VALID(2),
    ACCOUNT_NOT_VALID(3),
    EMAIL_PASSWORD_NOT_MATCH(4),
    WORKSPACE_NOT_FOUND(5),
    ACCOUNT_NOT_PART_OF_WORKSPACE_OR_SUSPEND(6),
    ACCOUNT_WAIT_FOR_DELETED(70),//status=2, account dang cho xoa
    ACCOUNT_BLOCKED(80),//status=-2, account bi khoa
    RPC_AUTHEN_FAILED(9),
    ;

    private final int value;

    LoginError(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
