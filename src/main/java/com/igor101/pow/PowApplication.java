package com.igor101.pow;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HexFormat;

public class PowApplication {

    private static final BigInteger ZERO_DIFFICULTY = BigInteger.valueOf(2).pow(256);
    public static void main(String[] args) {
        var data = "some message";

        var difficulty = ZERO_DIFFICULTY.shiftRight(30);
        var maxRounds = 100_000_000;

        Pow.tryToSolve(data, difficulty, maxRounds);
    }
}
