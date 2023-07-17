package com.igor101.records;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RecordsApp {
    public static void main(String[] args) {
        var user = new User(UUID.randomUUID(), "Igor", "igor@email.com",
                Instant.now(), List.of(new UserRole("Admin", "some admin")));

        var changedUser1 = Records.copy(user,
                Map.of("createdAt", Instant.now().plusMillis(1000),
                        "email", "gmail@igor.com"));
        var changedUser2 = Records.copy(user, Map.of("roles", List.of()));

        System.out.println("Source user: " + user);
        System.out.println("Changed user 1: " + changedUser1);
        System.out.println("Changed user 2: " + changedUser2);
        System.out.println("Type of source user: " + user.getClass());
        System.out.println("Type of user 1: " + changedUser1.getClass());
        System.out.println("First role of user 1: " + changedUser1.firstRole());
    }

    record User(UUID id,
                String name,
                String email,
                Instant createdAt,
                List<UserRole> roles) {

        Optional<UserRole> firstRole() {
            return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
        }

    }

    record UserRole(String name, String description) {
    }
}
