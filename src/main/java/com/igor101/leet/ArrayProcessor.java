package com.igor101.leet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayProcessor {

    static void runCases() {
        Case.cases().forEach(c -> {
            var actual = processArray(c.input);
            if (!actual.equals(c.expected)) {
                throw new RuntimeException("Expected to get %s result for %s input, but got %s"
                        .formatted(c.expected, Arrays.toString(c.input), actual));
            }
        });
    }

    public static List<Integer> processArray(int[] input) {
        var result = new ArrayList<Integer>();

        for (int num : input) {
            if (num < 0) {
                result.add(num);
            } else if (num != 0 && num <= result.size()) {
                result.remove(num - 1);
            }
        }

        return result;
    }

    record Case(int[] input, List<Integer> expected) {

        static List<Case> cases() {
            return List.of(
                    new Case(new int[]{-1, -2, -3, 2}, List.of(-1, -3)),
                    new Case(new int[]{-10, -22, 0, 1, 44, -14}, List.of(-22, -14))
            );
        }
    }
}
