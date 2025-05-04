package com.cogover.template.server.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huydn on 21/4/25 03:14
 */
@Getter
@Setter
public class RequestDto {

    protected boolean fromPublic;
    protected String clientIp;

}
