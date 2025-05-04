package com.cogover.template.server.network.rpc_server.handler;

import com.cogover.rpc.server.packet.response.impl.ResponseBodyErrorCode;

/**
 * @author huydn on 2/5/24 23:53
 */
public enum CommonError implements ResponseBodyErrorCode {

    NOT_INIT(-1),
    SUCCESS(0),
    CSRF_TOKEN_NOT_VALID(19000),
    ;

    private final int value;

    CommonError(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

}
