package com.cogover.template.server.controller.login;

import com.cogover.template.server.common.RequestDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huydn on 20/4/25 00:15
 */
@Getter
@Setter
public class LoginRequest extends RequestDto {

    private String email;
    private String password;
    private String continueUrl;
    private String deviceId;

}
