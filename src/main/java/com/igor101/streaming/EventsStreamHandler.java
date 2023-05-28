package com.igor101.streaming;

import com.igor101.events.Events;

public interface EventsStreamHandler {

    void subscribe(Events events);

    void consume();

}
