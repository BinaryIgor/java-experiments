package com.igor101.streaming;

import com.igor101.events.Events;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StreamingApp {

    private final Events events;
    private final EventsStreamHandler eventsStreamHandler;
    private final ScheduledExecutorService scheduler;
    private final long consumeDelaySeconds;

    public StreamingApp(Events events,
                        EventsStreamHandler eventsStreamHandler,
                        ScheduledExecutorService scheduler,
                        long consumeDelaySeconds) {
        this.events = events;
        this.eventsStreamHandler = eventsStreamHandler;
        this.scheduler = scheduler;
        this.consumeDelaySeconds = consumeDelaySeconds;
    }

    public StreamingApp(Events events,
                        EventsStreamHandler eventsStreamHandler,
                        long consumeDelaySeconds) {
        this(events, eventsStreamHandler, Executors.newSingleThreadScheduledExecutor(), consumeDelaySeconds);
    }

    public void start() {
        eventsStreamHandler.subscribe(events);
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                eventsStreamHandler.consume();
            } catch (Exception e) {
                System.err.println("Problem while consuming...");
                e.printStackTrace();
            }
        }, 0, consumeDelaySeconds, TimeUnit.SECONDS);
    }
}
