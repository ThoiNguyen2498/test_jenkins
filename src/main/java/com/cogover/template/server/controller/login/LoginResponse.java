package com.cogover.template.server.controller.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huydn on 20/4/25 00:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String continueUrl;
    private String accountAuthToken;

}
