package com.igor101.saga.choreography.ads;

import java.util.UUID;

public record Account(UUID id, String name, AccountInventory inventory, AccountState state) {

    public static Account newAccount(String name, AccountInventory inventory) {
        return new Account(UUID.randomUUID(), name, inventory, AccountState.created());
    }

    public Account ready() {
        return new Account(id, name, inventory, AccountState.ready());
    }

    public Account invalidInventory(String reason) {
        return new Account(id, name, inventory, AccountState.invalidInventory(reason));
    }
}
