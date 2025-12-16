package com.igor101.jobs;

import java.util.*;

public class InMemoryAlertNetwork implements AlertNetwork {

    private final Map<String, List<String>> servicesWithDependencies = new HashMap<>();
    private final Map<String, List<String>> dependenciesToServices = new HashMap<>();

    @Override
    public void addService(String service) {
        servicesWithDependencies.computeIfAbsent(service, $ -> new ArrayList<>());
    }

    @Override
    public void addDependency(String fromService, String toService) {
        // TODO: check uniqueness
        servicesWithDependencies.computeIfAbsent(toService, $ -> new ArrayList<>()).add(fromService);
        addService(fromService);

        dependenciesToServices.computeIfAbsent(fromService, $ -> new ArrayList<>()).add(toService);
    }

    @Override
    public List<String> getDependencies(String service) {
        return List.of();
    }

    @Override
    public List<String> findAlertPropagationPath(String source, String target) {
        var parent = new HashMap<String, String>();

        var queue = new ArrayDeque<String>();
        queue.add(source);
        var pathFound = false;


        while (!queue.isEmpty()) {
            var nextService = queue.poll();

            // early exit - first occurrence means the best path
            if (nextService.equals(target)) {
                pathFound = true;
                break;
            }

            for (var reachableService : dependenciesToServices.getOrDefault(nextService, List.of())) {
                if (!parent.containsKey(reachableService)) {
                    parent.put(reachableService, nextService);
                    queue.add(reachableService);
                }
            }
        }

        if (!pathFound) {
            return List.of();
        }

        var shortestPath = new LinkedList<String>();
        for (var previous = target; previous != null; previous = parent.get(previous)) {
            shortestPath.add(previous);
        }
        Collections.reverse(shortestPath);
        return shortestPath;
    }

    @Override
    public List<String> getAffectedServices(String source) {
        var affectedServices = new ArrayList<String>();

        var servicesToCheck = new PriorityQueue<String>();
        servicesToCheck.addAll(dependenciesToServices.getOrDefault(source, List.of()));
        while (!servicesToCheck.isEmpty()) {
            var nextService = servicesToCheck.poll();
            if (!affectedServices.contains(nextService)) {
                affectedServices.add(nextService);
            }

            var reachableServices = dependenciesToServices.getOrDefault(nextService, List.of());
            servicesToCheck.addAll(reachableServices);
        }

        return affectedServices;
    }

    @Override
    public List<Pair<String, String>> suggestContainmentEdges(String source) {
        return List.of();
    }
}
