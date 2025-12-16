package com.igor101.leet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class TaskScheduler {

    static int leastInterval(char[] tasks, int n) {
        var sortedTasks = new ArrayList<Character>();
        for (var t : tasks) {
            sortedTasks.add(t);
        }
        sortedTasks.sort(Comparator.naturalOrder());

        System.out.println(sortedTasks);

        var optimalTasksOrder = new ArrayList<Character>();

        Character lastTask = null;
        var idx = 0;
        while (!sortedTasks.isEmpty()) {
            if (idx >= sortedTasks.size()) {
                idx = 0;
            }
            var nextTask = sortedTasks.get(idx);
            if (!nextTask.equals(lastTask)) {
                optimalTasksOrder.add(nextTask);
                lastTask = nextTask;
                sortedTasks.remove(idx);
            } else {
                idx++;
            }
        }

        System.out.println("Optimal order: " + optimalTasksOrder);

        var tasksIndexes = new LinkedHashMap<Character, List<Integer>>();
        for (int i = 0; i < optimalTasksOrder.size(); i++) {
            var task = optimalTasksOrder.get(i);
            tasksIndexes.computeIfAbsent(task, $ -> new ArrayList<>()).add(i);
        }

        int leastIntervals = 0;
        for (var e : tasksIndexes.entrySet()) {
            var tIndexes = e.getValue();
            leastIntervals += 1;

            for (int i = 1; i < tIndexes.size(); i++) {
                var tIdx = tIndexes.get(i);
                var previousTIdx = tIndexes.get(i - 1);
                var sameTasksGap = tIdx - previousTIdx - 1;
                System.out.println("Gap: " + sameTasksGap + " n: " + n);
                if (sameTasksGap >= n) {
                    leastIntervals += 1;
                } else {
                    var idle = n - sameTasksGap;
                    System.out.println("Idle required: " + idle);
                    leastIntervals += (idle + 1);
                }
            }
        }

        return leastIntervals;
    }

    record Case(char[] tasks, int n, int intervals) {

        static List<Case> cases() {
            return List.of(
                    new Case(new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 2, 8),
                    new Case(new char[]{'A', 'C', 'A', 'B', 'D', 'B'}, 1, 6),
                    new Case(new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 3, 10)
            );
        }

    }
}
