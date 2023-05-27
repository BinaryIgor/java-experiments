package com.igor101.streaming;

import com.igor101.events.Events;
import com.igor101.streaming.handler.GroupedEmissionUnitClicksViewsBatchHandler;
import com.igor101.streaming.model.Click;
import com.igor101.streaming.model.Emission;
import com.igor101.streaming.model.View;
import com.igor101.streaming.repository.StatsRepository;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StreamingApp {

    private final Events events;
    private final StatsRepository statsRepository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final long consumeDelaySeconds;

    public StreamingApp(Events events,
                        StatsRepository statsRepository,
                        long consumeDelaySeconds) {
        this.events = events;
        this.statsRepository = statsRepository;
        this.consumeDelaySeconds = consumeDelaySeconds;
    }

    public void start() {
        var groupedEmissionUnitClickViewsBatchHandler = new GroupedEmissionUnitClicksViewsBatchHandler(statsRepository,
                t -> t.truncatedTo(ChronoUnit.DAYS));
        var stream = new EmissionClickViewStream(groupedEmissionUnitClickViewsBatchHandler);

        events.subscribe(Emission.class, stream::onEmission);
        events.subscribe(Click.class, stream::onClick);
        events.subscribe(View.class, stream::onView);

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                stream.consume();
            } catch (Exception e) {
                System.err.println("Problem while consuming...");
                e.printStackTrace();
            }
        }, 0, consumeDelaySeconds, TimeUnit.SECONDS);
    }
}
