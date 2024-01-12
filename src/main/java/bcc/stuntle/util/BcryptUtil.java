package bcc.stuntle.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class BcryptUtil {

    private static final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    public static String encode(String rawPassword){
        return bcrypt.encode(rawPassword);
    }

    public static boolean match(String rawPassword, String bcryptPassword){
        return bcrypt.matches(rawPassword, bcryptPassword);
    }
}
