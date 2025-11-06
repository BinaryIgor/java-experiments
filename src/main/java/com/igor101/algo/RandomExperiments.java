package com.igor101.algo;

import java.util.PriorityQueue;

public class RandomExperiments {
    public static void main(String[] args) {
        var queue = new PriorityQueue<String>();

        queue.add("Z");
        queue.add("C");
        queue.add("A");
        queue.add("D");

        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }
}
