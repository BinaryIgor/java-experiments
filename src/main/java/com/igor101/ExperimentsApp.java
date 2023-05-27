package com.igor101;

import com.igor101.streaming.TimeExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

public class ExperimentsApp {
    public static void main(String[] args) throws Exception{
        var now = Instant.now();
        System.out.println(now);
        System.out.println(now.truncatedTo(ChronoUnit.MINUTES));
        System.out.println(now.truncatedTo(ChronoUnit.HOURS));
        System.out.println(now.truncatedTo(ChronoUnit.DAYS));
        System.out.println(TimeExtension.instantTruncatedTo(now, ChronoUnit.MINUTES, 2));

//        var daySeconds = 60 * 60 * 24;
//
//        var nowSeconds = System.currentTimeMillis() / 1000;
//        var nowDiv = nowSeconds;
//
//        var time = nowSeconds % daySeconds;
//
//        System.out.println(time);
//        var hour = time / (60 * 60);
//        var minutes = (time - hour * 60 * 60) / 60;
//        var seconds =  time - (hour * 60 * 60)  - (minutes * 60);
//        System.out.println("%02d:%02d:%02d".formatted(hour, minutes, seconds));
    }

    private static <T> void checkType(Class<T> tClass) {
        System.out.println(tClass);
    }

    record SomeObject(String id, String name){}

    sealed interface SealedRecord permits Record1, Record2 {

    }

    record Record1() implements SealedRecord {}
    record Record2() implements SealedRecord {}
}
