package com.igor101.events;

public interface Events {

    <T> void subscribe(Class<T> event, EventHandler<T> handler);

    EventPublisher publisher();
}
