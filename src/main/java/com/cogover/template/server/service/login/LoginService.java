package com.cogover.template.server.service.login;

import com.cogover.template.server.controller.login.LoginRequest;
import com.cogover.template.server.controller.login.LoginResponse;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.common.ResponseDTO;
import com.cogover.template.server.repository.mongo.AuthTokenRepository;
import com.cogover.template.server.repository.mysql.common_db.AccountRepository;
import com.cogover.template.server.service.password_hash.PasswordHashBCryptService;
import com.cogover.exception.DbException;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

/*
 * @author huydn on 07/04/2024 23:18
 */
@Log4j2
public class LoginService {

    private final AccountRepository accountRepository;
    private final AuthTokenRepository tokenRepository;
    private final PasswordHashBCryptService passwordHashService;

    public LoginService(
            AccountRepository accountRepository,
            AuthTokenRepository tokenRepository,
            PasswordHashBCryptService passwordHashService
    ) {
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
        this.passwordHashService = passwordHashService;
    }

    public ResponseDTO<LoginResponse> process(LoginRequest request) throws DbException {
        log.info("process login request");

        //test goi qua module khac
//        Span rootSpan = TracerInterceptor.rootSpan(request);
//        String traceId = rootSpan.getSpanContext().getTraceId();
//        String spanId = rootSpan.getSpanContext().getSpanId();
        //
        JSONObject body = new JSONObject();
//        body.put("traceId", traceId);
//        body.put("spanId", spanId);
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        //neu request den tu internal, check Authorization header
        //VD case quen password, PHP co the goi service nay de lay token ma ko can truyen len "password"
        if (!request.isFromPublic()) {
//            boolean ok = RpcAuthenUtil.isAuthenticated(request);
//            if (!ok) {
//                return new DefaultResponseBody(LoginError.RPC_AUTHEN_FAILED);
//            }
        }

        //check email
        long start = System.currentTimeMillis();
        Account account = accountRepository.getAccountByEmail(request.getEmail(), true);
        if (account == null) {
            return new ResponseDTO<>(LoginError.EMAIL_PASSWORD_NOT_MATCH);
        }
        long time = System.currentTimeMillis() - start;
        log.info("getAccountByEmail from MySQL time: {} ms", time);

        //check password
        if (request.isFromPublic()) {
            //neu client gui len tu public thi moi can password
            String encodedPassFromDB = account.getPassword();
            String saltFromDb = account.getPasswordSalt();
            boolean match = passwordHashService.matches(request.getPassword(), saltFromDb, encodedPassFromDB);
            if (!match) {
                return new ResponseDTO<>(LoginError.EMAIL_PASSWORD_NOT_MATCH);
            }
        }

        //generate tokens
        AuthToken accountToken = tokenRepository.generateNewAccountToken(
                account,
                request.getDeviceId(),
                new JSONObject(),
                "clientIp"
        );

        LoginResponse res = new LoginResponse();
        res.setContinueUrl(request.getContinueUrl());
        res.setAccountAuthToken(accountToken.genJwt());

        return new ResponseDTO<>(0, "OK", res);
    }

}
