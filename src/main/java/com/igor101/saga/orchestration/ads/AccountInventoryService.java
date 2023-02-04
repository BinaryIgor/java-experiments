package com.igor101.saga.orchestration.ads;

import com.igor101.events.EventPublisher;
import com.igor101.events.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AccountInventoryService {

    private final Map<UUID, AccountInventory> inventories = new HashMap<>();
    private final EventPublisher eventPublisher;

    public AccountInventoryService(Events events) {
        eventPublisher = events.publisher();
    }

    //TODO reject if!
    public AccountInventory create(AccountInventory accountInventory) {
        var error = inventoryError(accountInventory);
        if (error == null) {
            inventories.put(accountInventory.accountId(), accountInventory);
            eventPublisher.publish(new AccountInventoryCreated(accountInventory.accountId()));
            return accountInventory;
        }

        eventPublisher.publish(new AccountInventoryRejected(accountInventory.accountId(), error));

        throw new RuntimeException(error);
    }

    private String inventoryError(AccountInventory inventory) {
        if (inventory.brandCodes() == null) {
            return "Null brand codes";
        }
        if (inventory.brandCodes().isEmpty() || inventory.brandCodes().size() > 10) {
            return "Brand codes can't be empty, and there can be max 10 of them";
        }
        return null;
    }

    public AccountInventory ofAccountId(UUID accountId) {
        return Optional.ofNullable(inventories.get(accountId))
                .orElseThrow(() -> new RuntimeException("Account inventory doesn't exist"));
    }

}
