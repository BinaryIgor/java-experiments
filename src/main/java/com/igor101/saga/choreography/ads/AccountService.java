package com.igor101.saga.choreography.ads;

import com.igor101.events.EventPublisher;
import com.igor101.events.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AccountService {

    private final Map<UUID, Account> accounts = new HashMap<>();
    private final EventPublisher eventPublisher;

    public AccountService(Events events) {
        eventPublisher = events.publisher();

        events.subscribe(InventoryCreated.class, e -> {
            Optional.ofNullable(accounts.get(e.id()))
                    .ifPresent(a -> {
                        accounts.put(a.id(), a.ready());
                    });
        });

        events.subscribe(InventoryRejected.class, e -> {
            Optional.ofNullable(accounts.get(e.id()))
                    .ifPresent(a -> {
                        accounts.put(a.id(), a.invalidInventory(e.reason()));
                    });
        });
    }

    public Account create(CreateAccount command) {
        var account = Account.newAccount(command.name(), command.inventory());

        accounts.put(account.id(), account);

        eventPublisher.publish(new CreateInventory(new Inventory(account.id(), command.inventory().brandCodes())));

        return account;
    }

    public Account getById(UUID id) {
        return Optional.ofNullable(accounts.get(id)).orElseThrow(() -> new RuntimeException("Account doesn't exist"));
    }
}
