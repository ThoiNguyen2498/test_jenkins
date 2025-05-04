package com.cogover.template.server.common;

import com.cogover.rpc.server.packet.response.impl.ResponseBodyErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huydn on 20/4/25 00:44
 */
@Data
@AllArgsConstructor
public class ResponseDTO<T> {

    private int r;
    private String msg;
    private T data;

    public ResponseDTO(ResponseBodyErrorCode errorCode) {
        this.r = errorCode.getValue();
        this.msg = errorCode.toString();
    }
}
