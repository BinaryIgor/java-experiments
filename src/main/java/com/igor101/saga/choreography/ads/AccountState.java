package com.igor101.saga.choreography.ads;

public record AccountState(String state, String reason) {

    public static AccountState created() {
        return new AccountState("CREATED", "Waiting for an inventory");
    }

    public static AccountState ready() {
        return new AccountState("READY", "inventory prepared");
    }

    public static AccountState invalidInventory(String reason) {
        return new AccountState("INVALID_INVENTORY", reason);
    }
}
