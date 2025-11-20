package com.igor101.leet;

import java.util.Arrays;
import java.util.List;

public class BestTimeToBuyAndSellStock2 {

    static void runCases() {
        Case.cases().forEach(c -> {
            var actual = maxProfit(c.prices);
            if (actual != c.output) {
                throw new RuntimeException("Got %d profit, but expected %d for %s prices"
                        .formatted(actual, c.output, Arrays.toString(c.prices)));
            }
        });
    }

    static int maxProfit(int[] prices) {
        if (prices.length <= 1) {
            return 0;
        }
        int totalProfit = 0;
        for (int i = 1; i < prices.length; i++) {
            totalProfit += Math.max(0, prices[i] - prices[i - 1]);
        }
        return totalProfit;
    }

    record Case(int[] prices, int output) {

        static List<Case> cases() {
            return List.of(
                    new Case(new int[]{7, 1, 5, 3, 6, 4}, 7),
                    new Case(new int[]{1, 2, 4, 5}, 4),
                    new Case(new int[]{7, 6, 4, 3, 1}, 0)
            );
        }
    }
}
