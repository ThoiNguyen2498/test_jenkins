package com.cogover.template.server.service.login;

import com.cogover.template.server.common.ResponseDTO;
import com.cogover.template.server.config.Config;
import com.cogover.template.server.controller.login.LoginRequest;
import com.cogover.template.server.controller.login.LoginResponse;
import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.database.entity.common_mongo.AuthToken;
import com.cogover.template.server.service.password_hash.PasswordHashBCryptService;
import com.cogover.exception.DbException;
import com.cogover.template.server.repository.mongo.AuthTokenRepository;
import com.cogover.template.server.repository.mysql.common_db.AccountRepository;
import com.cogover.rpc.server.packet.response.RpcResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author huydn on 07/04/2024 23:29
 */
class LoginServiceTest {

    private final static Logger log = LogManager.getLogger("LoginServiceTest");

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthTokenRepository tokenRepository;

    private LoginService loginService;
    private PasswordHashBCryptService passwordHashService;

    @BeforeEach
    void setUp() {
        Config.initLog4j();

        MockitoAnnotations.openMocks(this);

        passwordHashService = new PasswordHashBCryptService();

        loginService = new LoginService(
                accountRepository,
                tokenRepository,
                passwordHashService
        );
    }

    AuthToken genAuthToken(int type, String deviceId) {
        AuthToken authToken = new AuthToken();
        authToken.setAccountId("AC_HUY");
        authToken.setDeviceId(deviceId);
        authToken.setId(new ObjectId("6627fd0dfc7c5e0102987807"));
        authToken.setType(type);
        authToken.setExpiredTime((int) (System.currentTimeMillis() / 1000) + 3600);
        authToken.setSecretKey("secretKey-123");
        return authToken;
    }

    @Test
    void loginSuccessTest() throws DbException {
        String dbRawPasswd = "123456aA$";
        String reqPasswd = "123456aA$";
        String email = "huy@stringee.com";
        ResponseDTO<LoginResponse> readResponse = processLogin(email, dbRawPasswd, reqPasswd, 1);
        assertEquals(0, readResponse.getR());
    }

    @Test
    void loginInvalidCredentialsTest() throws DbException {
        String dbRawPasswd = "123456aA$";
        String reqPasswd = "123456aA$==";
        String email = "huy@stringee.com";
        ResponseDTO<LoginResponse> readResponse = processLogin(email, dbRawPasswd, reqPasswd, 1);
        assertEquals(LoginError.EMAIL_PASSWORD_NOT_MATCH.getValue(), readResponse.getR());
    }

    ResponseDTO<LoginResponse> processLogin(String email, String dbRawPasswd, String reqPasswd, int status) throws DbException {
        String salt = "salt";
        String deviceId = "device-123";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(reqPasswd);
        request.setContinueUrl("https://account.cogover.net/");
        request.setDeviceId(deviceId);
        request.setFromPublic(true);
        RpcResponse result = new RpcResponse(new DefaultHttpHeaders());

        //mock DB
        String dbPassword = passwordHashService.hash(dbRawPasswd, salt);

        //mock Account
        Account account = new Account();
        account.setId("AC_HUY");
        account.setEmail(email);
        account.setPasswordSalt(salt);
        account.setPassword(dbPassword);
        account.setStatus((byte) status);
        when(accountRepository.getAccountByEmail(anyString(), anyBoolean())).thenReturn(account);

        //mock generateNewAccountToken
        AuthToken accountToken = genAuthToken(1, deviceId);
        when(tokenRepository.generateNewAccountToken(
                eq(account),
                eq(deviceId),
                any(),
                any()
        )).thenReturn(accountToken);

        //mock generateNewWorkspaceToken
        AuthToken wsToken = genAuthToken(2, deviceId);
        when(tokenRepository.generateNewWorkspaceToken(
                eq(account),
                any(),
                eq(deviceId),
                any(),
                any(),
                any()
        )).thenReturn(wsToken);

        return loginService.process(request);
    }

}
