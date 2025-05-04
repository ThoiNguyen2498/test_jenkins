package com.cogover.template.server.exception;

import com.cogover.exception.CogoverException;

/**
 * @author huydn on 09/04/2024 23:14
 */
public class ServiceException extends CogoverException {

    public ServiceException(int r, String msg, Throwable cause) {
        super(r, msg, cause);
    }

}
