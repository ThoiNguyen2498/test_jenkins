package integration_test.service;

import com.cogover.exception.DbException;
import com.cogover.template.server.common.ResponseDTO;
import com.cogover.template.server.controller.login.LoginRequest;
import com.cogover.template.server.controller.login.LoginResponse;
import com.cogover.template.server.repository.mongo.AuthTokenRepositoryMongo;
import com.cogover.template.server.repository.mysql.common_db.AccountRepositoryMySQL;
import com.cogover.template.server.service.login.LoginError;
import com.cogover.template.server.service.login.LoginService;
import com.cogover.template.server.service.password_hash.PasswordHashBCryptService;
import integration_test.InitResourceForTest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author huydn on 26/5/24 01:13
 */
@Log4j2
class LoginServiceInterTest {

    @BeforeEach
    void setUp() {
        InitResourceForTest.init();
    }

    ResponseDTO<LoginResponse> processLogin(String email, String password) throws DbException {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setContinueUrl("https://account.cogover.net/");
        request.setDeviceId("device-abc-123");
        request.setFromPublic(true);

        LoginService loginService = new LoginService(
                new AccountRepositoryMySQL(),
                new AuthTokenRepositoryMongo(),
                new PasswordHashBCryptService()
        );
        ResponseDTO<LoginResponse> responseBody = loginService.process(request);
        log.info("Response: {}", responseBody.toString());
        return responseBody;
    }

    @Test
    void loginSuccessTest() throws DbException {
        ResponseDTO<LoginResponse> responseBody = processLogin("huy@stringee.com", "123456aA@");
        int r = responseBody.getR();
        assertEquals(LoginError.SUCCESS.getValue(), r);
    }

    @Test
    void loginIncorrectPasswordTest() throws DbException {
        ResponseDTO<LoginResponse> responseBody = processLogin("huy@stringee.com", "123");
        int r = responseBody.getR();
        assertEquals(LoginError.EMAIL_PASSWORD_NOT_MATCH.getValue(), r);
    }
}
