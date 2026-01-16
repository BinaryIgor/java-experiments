package com.igor101.leet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TaskScheduler {

    static int leastInterval(char[] tasks, int n) {
        var taskFrequencies = new HashMap<Character, Integer>();
        for (var t : tasks) {
            taskFrequencies.merge(t, 1, Integer::sum);
        }

        var frequenciesToTasks = new HashMap<Integer, List<Character>>();

        taskFrequencies.forEach((t, freq) -> {
            frequenciesToTasks.computeIfAbsent(freq, k -> new ArrayList<>()).add(t);
        });

        var sortedFrequencies = frequenciesToTasks.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        var optimalSchedule = new ArrayList<Character>();

        var nextFrequencyIdx = 0;
        while (!taskFrequencies.isEmpty()) {
            var frequency = sortedFrequencies.get(nextFrequencyIdx);

            var fTasks = frequenciesToTasks.getOrDefault(frequency, List.of());

            // TODO: optimize
            fTasks.forEach(t -> {
                optimalSchedule.add(t);
                var taskRemainingInstances = taskFrequencies.getOrDefault(t, 0);
                if (taskRemainingInstances > 1) {
                    taskFrequencies.put(t, taskRemainingInstances - 1);
                } else {
                    taskFrequencies.remove(t);
                }
            });

            nextFrequencyIdx++;
            if (nextFrequencyIdx == sortedFrequencies.size()) {
                nextFrequencyIdx = 0;
            }
        }

        var leastIntervals = 0;

        System.out.println("Optimal schedule: " + optimalSchedule);

        for (int i = 0; i < optimalSchedule.size(); i++) {
            System.out.println("Optimal: " + optimalSchedule);
            var task = optimalSchedule.get(i);
            if (task == null) {
                continue;
            }
            var idle = 0;
            for (int j = 1; j <= n && (i + j) < optimalSchedule.size(); j++) {
                var nextTask = optimalSchedule.get(i + j);
                if (task.equals(nextTask)) {
                    idle = n + 1 - j;
                    System.out.println("Idle: " + idle + " For: " + task + ", " + nextTask);
                    for (int k = 0; k < idle; k++) {
                        optimalSchedule.add(i + j, null);
                    }
                    break;
                }
            }
            leastIntervals += (1 + idle);
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
