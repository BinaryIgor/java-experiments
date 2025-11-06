package com.igor101;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Random;
import java.util.regex.Pattern;

public class Experiments {
    private static final String VARIABLE_PATTERN_STR = "\\$([a-zA-Z0-9-_.]+)";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_PATTERN_STR);
    private static final Random RANDOM = new SecureRandom();


    public static void main(String[] args) throws Exception {
        var str = """
            $var1 some text
            some more text and, finally, $var2
            """;

        var matcher = VARIABLE_PATTERN.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group());
            System.out.println(matcher.group(1));
            System.out.println("REst:");
            System.out.println(str.substring(matcher.end()));
        }


        System.out.println();
        System.out.println(randomId());
        System.out.println(randomId());
        System.out.println(randomId());

    }

    private static String randomId() {
        var bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

}
