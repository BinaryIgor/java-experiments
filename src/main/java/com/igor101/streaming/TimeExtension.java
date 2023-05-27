package com.igor101.streaming;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class TimeExtension {

    public static Instant instantTruncatedTo(Instant instant,
                                             ChronoUnit unit,
                                             int amount) {
        var initiallyTruncated = instant.truncatedTo(unit);

        if (unit == ChronoUnit.MINUTES) {
            //TODO: validate amount!
            var parts = 60 / amount;
            var part = 60 / parts;

            var localDateTime = LocalDateTime.ofInstant(initiallyTruncated, ZoneId.of("UTC"));

            var neededMinute = 0;

            for (int i = parts; i >= 0; i--) {
                var minute = part * i;
                if (localDateTime.getMinute() > minute) {
                    neededMinute = minute;
                    break;
                }
            }

            System.out.println("Part = " + part);
            System.out.println("Needed minute = " + neededMinute);

            var truncatedLocalDateTime = localDateTime.withMinute(neededMinute);

            return truncatedLocalDateTime.toInstant(ZoneOffset.UTC);
        }


        return initiallyTruncated;

    }
}
