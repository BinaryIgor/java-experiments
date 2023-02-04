package com.igor101.saga.choreography.ads;

import com.igor101.events.EventPublisher;
import com.igor101.events.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InventoryService {

    private final Map<UUID, Inventory> inventories = new HashMap<>();
    private final EventPublisher eventPublisher;

    public InventoryService(Events events) {
        eventPublisher = events.publisher();

        events.subscribe(CreateInventory.class, e -> {
            var inv = e.inventory();
            var error = inventoryError(inv);

            if (error == null) {
                inventories.put(inv.id(), inv);
                eventPublisher.publish(new InventoryCreated(inv.id()));
            } else {
                eventPublisher.publish(new InventoryRejected(inv.id(), error));
            }
        });
    }

    private String inventoryError(Inventory inventory) {
        if (inventory.brandCodes() == null) {
            return "Null brand codes";
        }
        if (inventory.brandCodes().isEmpty() || inventory.brandCodes().size() > 10) {
            return "Brand codes can't be empty, and there can be max 10 of them";
        }

        return null;
    }

    public Inventory getById(UUID id) {
        return Optional.ofNullable(inventories.get(id)).orElseThrow(() -> new RuntimeException("Inventory doesn't exist"));
    }
}
