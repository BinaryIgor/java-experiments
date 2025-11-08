package com.igor101.leet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Solution {
    public static void main(String[] args) throws Exception {
        NumberOfIslands.runCases();
    }

    static void printCacheList(LRUCache cache) {
        var node = cache.head();
        while (node != null) {
            System.out.println(node);
            node = node.nextValue();
        }
        System.out.println("-----");
        System.out.println();
    }


    public boolean isValid(String s) {
        return false;
    }

    public void validateTwoSum(TwoSumCase twoSumCase) {
        var result = twoSum(twoSumCase.nums, twoSumCase.target);
        if (!Arrays.equals(result, twoSumCase.expectedResult)) {
            throw new RuntimeException("Expected %s but got %s, for %s case"
                    .formatted(Arrays.toString(twoSumCase.expectedResult),
                            Arrays.toString(result), twoSumCase));
        }
    }

    public int[] twoSum(int[] nums, int target) {
        var numsToIndexCache = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; i++) {
            var needed = target - nums[i];
            var neededIdx = numsToIndexCache.get(needed);
            if (neededIdx != null) {
                return new int[]{neededIdx, i};
            }
            numsToIndexCache.put(nums[i], i);
        }
        return new int[0];
    }

    record TwoSumCase(int[] nums, int target, int[] expectedResult) {

        public static List<TwoSumCase> cases() {
            return List.of(
                    new TwoSumCase(new int[]{2, 7, 11, 15}, 9, new int[]{0, 1}),
                    new TwoSumCase(new int[]{3, 2, 4}, 6, new int[]{1, 2}),
                    new TwoSumCase(new int[]{3, 3}, 6, new int[]{0, 1})
            );
        }

        @Override
        public String toString() {
            return "TestCase[nums=%s, target=%d, expectedResult=%s]"
                    .formatted(Arrays.toString(nums), target, Arrays.toString(expectedResult));
        }
    }
}
