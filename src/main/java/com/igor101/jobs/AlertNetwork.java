package com.igor101.jobs;

import java.util.List;

public interface AlertNetwork {

    void addService(String service);

    void addDependency(String fromService, String toService); // Directed edge

    List<String> getDependencies(String service);

    List<String> findAlertPropagationPath(String source, String target);

    List<String> getAffectedServices(String source);

    List<Pair<String, String>> suggestContainmentEdges(String source); // Bonus
}
