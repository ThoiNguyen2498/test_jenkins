package com.cogover.template.server.exception;

import com.cogover.exception.CogoverException;

/**
 * @author huydn on 08/04/2024 00:25
 */
public class EncryptionException extends CogoverException {

    public EncryptionException(int r, String msg, Throwable cause) {
        super(r, msg, cause);
    }

}
