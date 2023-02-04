package com.igor101.saga.orchestration;

import com.igor101.events.Events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SagaDefinition {

    private final Events events;
    private final List<SagaStep> steps = new ArrayList<>();

    public SagaDefinition(Events events) {
        this.events = events;
    }

    public <Success, Failure, State> SagaDefinition addStep(SagaStep<Success, Failure, State> step) {
        steps.add(step);
        var stepIdx = steps.size() - 1;

        events.subscribe(step.successEvent(), e -> {
            var nextStepIdx = stepIdx + 1;
            if (nextStepIdx < steps.size()) {
                var nextStep = steps.get(nextStepIdx);
                System.out.printf("Executing next saga step: %s!%n", nextStep.name());
                executeSaga(nextStep, s -> nextStep.execute().accept(s));
            } else {
                System.out.println("Saga ended");
            }
        });
        events.subscribe(step.failureEvent(), e -> {
            var previousStepIdx = stepIdx - 1;
            if (previousStepIdx >= 0) {
                var previousStep = steps.get(previousStepIdx);
                System.out.printf("Saga failed, compensating previous step: %s!%n", previousStep.name());
                executeSaga(previousStep, s -> previousStep.compensate().accept(s));
            } else {
                System.out.println("First step of saga failed!");
            }
        });

        return this;
    }

    private void executeSaga(SagaStep step, Consumer<SagaState> toExecute) {
        try {
            toExecute.accept(step.state());
        } catch (Exception e) {
            System.out.println("Should retry...");
        }
    }

    public void execute() {
        if (steps.isEmpty()) {
            throw new RuntimeException("Can't execute empty saga!");
        }
        var firstStep = steps.get(0);

        System.out.println("Starting saga with..." + firstStep.name());

        firstStep.execute().accept(firstStep.state());
    }

}
