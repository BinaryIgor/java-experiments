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
        var alertPropagationPath = new ArrayList<String>();
        var shortestPropagationPath = List.<String>of();

        var servicesToCheck = new PriorityQueue<String>();
        servicesToCheck.add(source);
        while (!servicesToCheck.isEmpty()) {
            var nextService = servicesToCheck.poll();
            alertPropagationPath.add(nextService);
            if (nextService.equals(target)) {
                if (shortestPropagationPath.isEmpty() || alertPropagationPath.size() < shortestPropagationPath.size()) {
                    shortestPropagationPath = alertPropagationPath;
                }
                alertPropagationPath = new ArrayList<>();
                alertPropagationPath.add(source);
            } else {
                var reachableServices = dependenciesToServices.getOrDefault(nextService, List.of());
                servicesToCheck.addAll(reachableServices);
            }
        }

        return shortestPropagationPath;
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
