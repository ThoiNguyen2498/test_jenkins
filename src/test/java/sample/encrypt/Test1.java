package sample.encrypt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author huydn on 07/04/2024 23:55
 */
public class Test1 {

    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123456"));

        boolean ok = bCryptPasswordEncoder.matches("123456", "$2a$10$Ixb83wfjY6eyUKcoMw8Qpece.gily24f5ueg8S7FyaBF8.fNd.9bO");

        System.out.println(ok);
    }

}
