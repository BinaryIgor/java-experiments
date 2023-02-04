package com.igor101.saga.orchestration;

import java.util.function.Consumer;

public record SagaStep<Success, Failure, State>(String name,
                                                Consumer<SagaState<State>> execute,
                                                Consumer<SagaState<State>> compensate,
                                                Class<Success> successEvent,
                                                Consumer<Success> onSuccess,
                                                Class<Failure> failureEvent,
                                                Consumer<Failure> onFailure,
                                                SagaState<State> state) {

    public SagaStep(String name,
                    Consumer<SagaState<State>> execute,
                    Consumer<SagaState<State>> compensate,
                    Class<Success> successEvent,
                    Class<Failure> failureEvent,
                    SagaState<State> state) {
        this(name, execute, compensate, successEvent, null, failureEvent, null, state);
    }

}
