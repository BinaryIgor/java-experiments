package com.igor101.jobs;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryAlertNetworkTest {

    @Test
    void findsAlertPropagationPath() {
        var alertNetwork = new InMemoryAlertNetwork();

        alertNetwork.addDependency("A", "B");
        alertNetwork.addDependency("B", "D");
        alertNetwork.addDependency("A", "D");
        alertNetwork.addDependency("D", "C");

        Assertions.assertThat(alertNetwork.findAlertPropagationPath("A", "C"))
                .isIn(List.of("A", "B", "C"),
                        List.of("A", "D", "C"));
    }

    @Test
    void doesNotFindAlertPropagationPath() {
        var alertNetwork = new InMemoryAlertNetwork();

        alertNetwork.addDependency("A", "B");
        alertNetwork.addDependency("B", "C");
        alertNetwork.addDependency("D", "C");

        Assertions.assertThat(alertNetwork.findAlertPropagationPath("A", "D")).isEmpty();
    }

    @Test
    void returnsAffectedServices() {
        var alertNetwork = new InMemoryAlertNetwork();
        alertNetwork.addDependency("A", "B");
        alertNetwork.addDependency("B", "C");
        alertNetwork.addDependency("A", "D");
        alertNetwork.addDependency("D", "C");

        Assertions.assertThat(alertNetwork.getAffectedServices("A"))
                .isEqualTo(List.of("B", "C", "D"));
    }
}
