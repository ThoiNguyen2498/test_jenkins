package com.cogover.template.server.service.password_hash;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * <a href="https://www.baeldung.com/java-password-hashing">java-password-hashing</a>
 *
 * @author huydn on 07/04/2024 23:33
 */
public class PasswordHashBCryptService implements PasswordHashService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordHashBCryptService() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    /*
     *Thuat toan  BCrypt co san salt o dau chuoi encodedPass, tuy nhien van them salt o day de dam bao App ko loi~ khi
     * thay doi DB truong salt
     */
    public String hash(String plainTextPassword, String salt) {
        return bCryptPasswordEncoder.encode(catPassword(plainTextPassword, salt));
    }

    @Override
    public boolean matches(String plainTextPassword, String salt, String encodedPassword) {
        return bCryptPasswordEncoder.matches(catPassword(plainTextPassword, salt), encodedPassword);
    }

    private String catPassword(String plainTextPassword, String salt) {
        return salt + "_" + plainTextPassword;
    }
}
