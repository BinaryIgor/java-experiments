package com.igor101.leet;

import java.util.ArrayList;
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
        if (coins.length == 0) {
            return amount == 0 ? 0 : -1;
        }
        if (amount == 0) {
            return 0;
        }

        Arrays.sort(coins);
//
//        var solution = Integer.MAX_VALUE;
//        for (int i = coins.length - 1; i >= 0; i--) {
//            var s = checkCoins(coins, amount, i, 0, 0, solution);
//            System.out.println("Solution: " + s);
//            if (s != -1 && s < solution) {
//                solution = s;
//            }
//        }
//
//        return solution == Integer.MAX_VALUE ? -1 : solution;
//        var bestSolution = -1;
//        for (int i = coins.length - 1; i >= 0; i--) {
//            var solution = findCoinChange(coins, amount, i, 0, 0);
//            if (solution != -1 && (bestSolution == -1 || solution < bestSolution)) {
//                bestSolution = solution;
//            }
//        }


        var solutions = new PriorityQueue<Integer>();

        findCoinChange(coins, amount, coins.length - 1, 0, 0, solutions);

        System.out.println("Solutions: " + solutions);

        return solutions.isEmpty() ? -1 : solutions.peek();
    }

    private static void findCoinChange(int[] coins, int amount, int startIndex,
                                      int currentCoins, int currentAmount,
                                      PriorityQueue<Integer> solutions) {
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
                findCoinChange(coins, amount, i, nextSolution, amountWithCoin, solutions);
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
                    new Case(new int[]{1, 2, 5}, 11, 3),
                    new Case(new int[]{2}, 3, -1),
                    new Case(new int[]{186, 419, 83, 408}, 6249, 20),
                    new Case(new int[]{1}, 0, 0),
                    new Case(new int[]{1, 2, 4, 5}, 8, 2),
                    new Case(new int[]{1, 2, 4, 5}, 11, 3),
                    new Case(new int[]{1, 2}, 200, 100)
            );
        }
    }
}
