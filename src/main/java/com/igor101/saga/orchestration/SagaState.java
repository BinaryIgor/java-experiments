package com.igor101.saga.orchestration;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SagaState<T> {

    private final AtomicReference<T> state = new AtomicReference<>();

    public void set(T state) {
        this.state.set(state);
    }

    public Optional<T> get() {
        return Optional.ofNullable(state.get());
    }

    public T getOrThrow() {
        return get().orElseThrow(() -> new RuntimeException("Expected to have state, but was null!"));

    }
}
