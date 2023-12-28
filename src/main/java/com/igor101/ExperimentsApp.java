package com.igor101;

import java.util.UUID;

public class ExperimentsApp {
    public static void main(String[] args) {
        var a = new User(UUID.randomUUID(), "Igor");
        System.out.println(a);
    }

    record User(UUID id, String name) {
    }
}
