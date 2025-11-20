package com.igor101.leet;

import java.util.*;
import java.util.stream.Collectors;

public class _CourseSchedule {

    static void runCases() {
        Case.cases().forEach(c -> {
            var canFinish = canFinish(c.courses, c.prerequisites);
            if (canFinish != c.canFinish) {
                throw new RuntimeException("Expected canFinish %b for %s prerequisites, but got %b"
                        .formatted(c.canFinish, Arrays.deepToString(c.prerequisites), canFinish));
            }
        });
    }

    static boolean canFinish(int numCourses, int[][] prerequisites) {
        if (prerequisites.length == 0) {
            return true;
        }

        var graph = new HashMap<Integer, Node>();

        for (int i = 0; i < prerequisites.length; i++) {
            var nodeWithPotentialDependency = prerequisites[i];
            var node = nodeWithPotentialDependency[0];

            var nodeInGraph = graph.computeIfAbsent(node, $ -> new Node(node));

            if (nodeWithPotentialDependency.length > 1) {
                var nodeDep = nodeWithPotentialDependency[1];
                var nodeDepInGraph = graph.computeIfAbsent(nodeDep, $ -> new Node(nodeDep));
                nodeInGraph.dependencies.add(nodeDepInGraph);
            }
        }

        var orderedByDepsGraph = new PriorityQueue<Node>(numCourses, Comparator.comparingInt(a -> a.dependencies.size()));
        orderedByDepsGraph.addAll(graph.values());

        var nodeToDependent = new HashMap<Integer, List<Node>>();
        graph.values().forEach(node -> {
            node.dependencies.forEach(d -> {
                nodeToDependent.computeIfAbsent(d.value, $ -> new ArrayList<>()).add(node);
            });

        });

        System.out.println("For prerequisites: " + Arrays.deepToString(prerequisites));
        System.out.println("Computed graph:");
        graph.forEach((k, v) -> System.out.println(k + ": " + v.dependencies));
        System.out.println("Note do depenent:");
        nodeToDependent.forEach((k, v) -> System.out.println(k + ": " + v));

        var finishedCourses = new HashSet<Integer>();

        while (!orderedByDepsGraph.isEmpty()) {
            var node = orderedByDepsGraph.poll();
            System.out.println(node);
            var depsResolved = resolveDependencies(finishedCourses, node);
            if (depsResolved) {
                // TODO: resolve dependents!
                finishedCourses.add(node.value);
                nodeToDependent.getOrDefault(node.value, List.of())
                        .forEach(nd -> {
                            var ddepsResolved = resolveDependencies(finishedCourses, nd);
                            if (ddepsResolved) {
                                System.out.println("Resolved: " + nd);
                                finishedCourses.add(nd.value);
                            }
                        });

            } else {
                return false;
            }
        }

        return true;
    }

    private static boolean resolveDependencies(Set<Integer> resolvedDependencies, Node node) {
        if (node.dependenciesResolved) {
            return true;
        }
        return resolveDependencies(resolvedDependencies, node, node);
    }

    private static boolean resolveDependencies(Set<Integer> resolvedDependencies, Node node, Node parent) {
        var resolved = true;
        for (var d : node.dependencies) {
            if (parent.value == d.value) {
                // cycle
                resolved = false;
                break;
            }
            if (!resolvedDependencies.contains(d.value)) {
                System.out.println("About to check deps of: " + d + " resolved: " + d.dependenciesResolved);
                resolved = resolveDependencies(resolvedDependencies, d, node);
            }
        }

        node.dependenciesResolved = resolved;

        return resolved;
    }

    static class Node {

        final int value;
        List<Node> dependencies = new ArrayList<>();
        boolean dependenciesResolved = false;

        Node(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node[value=%d, deps=(%d:%s)]".formatted(value, dependencies.size(),
                    dependencies.stream().map(d -> String.valueOf(d.value))
                            .collect(Collectors.joining(",")));
        }
    }

    record Case(int courses, int[][] prerequisites, boolean canFinish) {

        static List<Case> cases() {
            return List.of(
                    new Case(8, new int[][]{{1, 0}, {2, 6}, {1, 7}, {5, 1}, {6, 4}, {7, 0}, {0, 5}}, true),
                    new Case(2, new int[][]{{1, 0}}, true),
                    new Case(2, new int[][]{{1, 0}, {0, 1}}, false),
                    new Case(3, new int[][]{{1, 0}, {1}, {1, 2}}, true),
                    new Case(4, new int[][]{{0}, {1}, {2}, {3, 2}}, true),
                    new Case(3, new int[][]{{0}, {0, 1}, {1, 2}}, true),
                    new Case(1, new int[][]{}, true),
                    new Case(5, new int[][]{{1, 4}, {2, 4}, {3, 1}, {3, 2}}, true)
            );
        }
    }
}
