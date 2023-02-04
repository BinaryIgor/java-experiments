package com.igor101.events;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEventsTest {

    private InMemoryEvents events;

    @BeforeEach
    void setup() {
        events = new InMemoryEvents();
    }

    @Test
    void shouldPublishEventsToMultipleSubscribers() {
        var publisher = events.publisher();

        var stringSubscriber1 = new ArrayList<String>();
        var stringSubscriber2 = new ArrayList<String>();
        var someEventSubscriber1 = new ArrayList<SomeEvent>();
        var someEventSubscriber2 = new ArrayList<SomeEvent>();

        events.subscribe(String.class, stringSubscriber1::add);
        events.subscribe(String.class, stringSubscriber2::add);
        events.subscribe(SomeEvent.class, someEventSubscriber1::add);
        events.subscribe(SomeEvent.class, someEventSubscriber2::add);

        Assertions.assertTrue(stringSubscriber1.isEmpty());
        Assertions.assertTrue(stringSubscriber2.isEmpty());
        Assertions.assertTrue(someEventSubscriber1.isEmpty());
        Assertions.assertTrue(someEventSubscriber2.isEmpty());

        var stringEvents = List.of("event1", "event2");
        var someEventEvents = List.of(new SomeEvent(1, "some-name"),
                new SomeEvent(22, "another-name"));

        stringEvents.forEach(publisher::publish);
        someEventEvents.forEach(publisher::publish);

        Assertions.assertEquals(stringSubscriber1, stringEvents);
        Assertions.assertEquals(stringSubscriber2, stringEvents);
        Assertions.assertEquals(someEventSubscriber1, someEventEvents);
        Assertions.assertEquals(someEventSubscriber2, someEventEvents);
    }

    private record SomeEvent(long id, String name) {
    }
}
