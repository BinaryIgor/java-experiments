package com.igor101.events;

public interface EventPublisher {
    <T> void publish(T event);
}
