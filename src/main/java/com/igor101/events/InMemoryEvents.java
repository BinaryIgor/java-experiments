package com.igor101.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryEvents implements Events {

    private final Map<Class<?>, Collection<EventHandler<Object>>> eventsHandlers = new ConcurrentHashMap<>();

    private final AtomicReference<EventPublisher> publisher = new AtomicReference<>();

    private EventPublisher newPublisher() {
        return new EventPublisher() {
            @Override
            public <T> void publish(T event) {
                var exceptions = new ArrayList<Exception>();

                eventsHandlers.getOrDefault(event.getClass(), List.of())
                        .forEach(eh -> handleEvent(eh, event, exceptions));

                throwExceptionIf(exceptions, event.getClass());
            }

            private <T> void handleEvent(EventHandler<Object> handler,
                                         T event,
                                         List<Exception> exceptions) {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    System.err.printf("Problem while handling event %s event...%n", event.getClass());
                    e.printStackTrace();
                    exceptions.add(e);
                }
            }

            private void throwExceptionIf(List<Exception> exceptions, Class<?> event) {
                if (exceptions.size() == 1) {
                    throw new RuntimeException(exceptions.get(0));
                } else if (exceptions.size() > 1) {
                    var combinedException = new RuntimeException(
                            "There were problems while handling %s event".formatted(event));

                    exceptions.forEach(combinedException::addSuppressed);

                    throw combinedException;
                }
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> event, EventHandler<T> handler) {
        eventsHandlers.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>())
                .add((EventHandler<Object>) handler);
    }

    @Override
    public EventPublisher publisher() {
        if (publisher.get() == null) {
            publisher.set(newPublisher());
        }
        return publisher.get();
    }
}
