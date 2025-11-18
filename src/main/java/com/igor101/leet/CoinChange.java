package com.igor101.leet;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class CoinChange {

    static void runCases() {
        Case.cases().forEach(c -> {
            System.out.println();
            var coins = coinChange(c.coins, c.amount);
            if (coins != c.expected) {
                throw new RuntimeException("Got %d coins for %s, %d input, but %d expected"
                        .formatted(coins, Arrays.toString(c.coins), c.amount, c.expected));
            }
        });
    }

    static int coinChange(int[] coins, int amount) {
        final int INF = 1 << 30;

        int numberOfCoins = coins.length;
        int targetAmount = amount;
        int[][] dp = new int[numberOfCoins + 1][targetAmount + 1];

        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }

        dp[0][0] = 0;

        for (int i = 1; i <= numberOfCoins; i++) {
            for (int j = 0; j <= targetAmount; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j >= coins[i - 1]) {
                    // Take minimum between not using current coin and using it
                    // When using current coin: add 1 to the count and reduce amount by coin value
                    dp[i][j] = Math.min(dp[i][j], dp[i][j - coins[i - 1]] + 1);
                }
            }
        }

        return dp[numberOfCoins][targetAmount] == INF ? -1 : dp[numberOfCoins][targetAmount];
    }

    private static void findCoinChange(int[] coins, int amount, int startIndex,
                                       int currentCoins, int currentAmount,
                                       PriorityQueue<Integer> solutions,
                                       boolean[][] checkedSolutions) {
        for (int i = startIndex; i >= 0; i--) {
            var coin = coins[i];
            var amountWithCoin = currentAmount + coin;
            System.out.println("Checking: " + amount + ", coin: " + coin + ", currentCoins: " + currentCoins + ", currentAmount: " + currentAmount);
            System.out.println("With coin: " + amountWithCoin);
            var nextSolution = currentCoins + 1;
            if (amountWithCoin == amount) {
                System.out.println("Found solution - " + (nextSolution));
                solutions.add(nextSolution);
            }

            if (amountWithCoin < amount && (solutions.isEmpty() || nextSolution < solutions.peek())) {
                findCoinChange(coins, amount, i, nextSolution, amountWithCoin, solutions, checkedSolutions);
            }
        }
    }

    private static int checkCoins(int[] coins, int amount, int coinIdx, int currentCoins, int currentAmount, int bestSolution) {
        if (coinIdx < 0 || currentCoins >= bestSolution) {
            return -1;
        }
        var coin = coins[coinIdx];
        var withCoinAmount = coin + currentAmount;
        System.out.println("Exploring: " + coin + ", currentCoins: " + currentCoins + ", currentAmount: " + currentAmount + " withCoin amount: " + withCoinAmount);
        if (withCoinAmount == amount) {
            return currentCoins + 1;
        }
        if (withCoinAmount < amount) {
            return checkCoins(coins, amount, coinIdx, currentCoins + 1, withCoinAmount, bestSolution);
        }
        return checkCoins(coins, amount, coinIdx - 1, currentCoins, currentAmount, bestSolution);
    }

    record Case(int[] coins, int amount, int expected) {

        static List<Case> cases() {
            return List.of(
                    new Case(new int[]{411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422}, 9864, 24),
                    new Case(new int[]{1, 2, 5}, 11, 3),
                    new Case(new int[]{1, 2, 5, 8}, 63, 9)
//                    new Case(new int[]{2}, 3, -1),
//                    new Case(new int[]{186, 419, 83, 408}, 6249, 20),
//                    new Case(new int[]{1}, 0, 0),
//                    new Case(new int[]{1, 2, 4, 5}, 8, 2),
//                    new Case(new int[]{1, 2, 4, 5}, 11, 3),
//                    new Case(new int[]{1, 2}, 200, 100)
            );
        }
    }
}
