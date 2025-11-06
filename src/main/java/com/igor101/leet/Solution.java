package com.igor101.leet;

import java.time.Instant;
import java.util.*;

public class Solution {
    public static void main(String[] args) throws Exception {
//        ValidParentheses.runCases();
        var cache = new LRUCache(2);
        cache.put(1, 11);
        cache.put(2, 12);
        cache.get(1);
        cache.put(3, 13);


        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
        System.out.println(cache.get(3));
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
