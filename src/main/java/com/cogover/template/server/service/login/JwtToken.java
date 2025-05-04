package com.cogover.template.server.service.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

/**
 * @author huydn on 08/04/2024 00:22
 */
@Log4j2
public class JwtToken {

    private JwtToken(){
    }

    public static String generateToken(AuthToken authToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(authToken.getSecretKey());

            String tokenId = authToken.getId().toHexString();

            return JWT.create()
                    .withIssuer("Cogover")
                    .withJWTId(tokenId)
                    .withExpiresAt(new Date(authToken.getExpiredTime() * 1000L))
                    .withClaim("refreshTimeout", authToken.getRefreshTimeout())
                    .withClaim("accountId", authToken.getAccountId())
                    .withClaim("workspaceId", authToken.getWorkspaceId())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
