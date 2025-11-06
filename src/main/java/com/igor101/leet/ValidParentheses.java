package com.igor101.leet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ValidParentheses {

    private static final List<Character> OPENING = List.of('(', '{', '[');
    private static final List<Character> CLOSING = List.of(')', '}', ']');
    private static final Map<Character, Character> OPENING_TO_CLOSING = Map.of(
            '(', ')',
            '{', '}',
            '[', ']'
    );

    public static void runCases() {
        ValidParenthesesCase.cases()
                .forEach(c -> {
                    var actual = isValid(c.string());
                    if (actual != c.isValid()) {
                        throw new RuntimeException("Expected %s isValid but got %s for %s string"
                                .formatted(c.isValid(), actual, c.string()));
                    }
                });
    }

    public static boolean isValid(String s) {
        if (s.length() < 2) {
            return false;
        }

        var opening = new LinkedList<Character>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (OPENING_TO_CLOSING.containsKey(c)) {
                opening.add(c);
            } else {
                var lastOpening = opening.pollLast();
                if (lastOpening == null || !OPENING_TO_CLOSING.get(lastOpening).equals(c)) {
                    return false;
                }
            }
        }

        return opening.isEmpty();
    }

    record ValidParenthesesCase(String string, boolean isValid) {

        public static List<ValidParenthesesCase> cases() {
            return List.of(
                    new ValidParenthesesCase("()", true),
                    new ValidParenthesesCase("()[]{}", true),
                    new ValidParenthesesCase("()", true),
                    new ValidParenthesesCase("([)]", false)
            );
        }
    }
}
