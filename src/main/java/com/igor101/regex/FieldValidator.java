package com.igor101.regex;

import java.util.regex.Pattern;

public class FieldValidator {

    private static final Pattern INTEGER_PATTERN =
            Pattern.compile("^-?[0-9]+$");
    private static final Pattern DOUBLE_PATTERN =
            Pattern.compile("^-?\\d+[,.]\\d+$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-z][a-zA-z0-9-_]{2,19}$");

    public static boolean isValidInteger(String string) {
        return string != null && INTEGER_PATTERN.matcher(string).matches();
    }

    public static boolean isValidDouble(String string) {
        return string != null &&
                (DOUBLE_PATTERN.matcher(string).matches()
                        || INTEGER_PATTERN.matcher(string).matches());
    }

    public static boolean isValidName(String string) {
        return string != null && NAME_PATTERN.matcher(string).matches();
    }
}
