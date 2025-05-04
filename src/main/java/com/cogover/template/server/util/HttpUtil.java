package com.cogover.template.server.util;

import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author huydn on 17/4/24 13:38
 */
@Log4j2
public class HttpUtil {

    private HttpUtil(){
    }

    public static String[] decodeAuthorizationHeader(String headerValue) {
        if (headerValue != null && headerValue.toLowerCase().startsWith("basic")) {
            try {
                String base64Credentials = headerValue.substring("Basic".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                // credentials = username:password
                return credentials.split(":", 2);
            } catch (Exception e) {
                log.error(e, e);
            }
        }

        return new String[]{};
    }

}
