package com.cogover.template.server.service.password_hash;

/**
 * @author huydn on 07/04/2024 23:33
 */
public interface PasswordHashService {

    /*
     * Hash password voi salt de luu vao DB
     */
    String hash(String plainTextPassword, String salt);

    /*
     * Check xem encodedPassword co duoc sinh ra tu rawPassword ko?
     */
    boolean matches(String plainTextPassword, String salt, String encodedPassword);

}
