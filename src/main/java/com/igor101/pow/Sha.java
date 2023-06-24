package com.igor101.pow;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Sha {

    public static byte[] calculate256(String data) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var raw = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            //Always unsigned;
            raw[0] = 0;
            return raw;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
