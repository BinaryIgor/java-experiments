package com.igor101.events;

public interface EventHandler<T> {
    void handle(T event);
}
