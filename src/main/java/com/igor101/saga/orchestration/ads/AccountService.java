package com.igor101.saga.orchestration.ads;

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
    }

    public Account createAccount(Account account) {
        accounts.put(account.id(), account);

        eventPublisher.publish(new AccountCreated(account.id()));

        return account;
    }

    public void deleteAccount(UUID id) {
        accounts.remove(id);
    }

    public Account ofId(UUID id) {
        return Optional.ofNullable(accounts.get(id)).orElseThrow(() -> new RuntimeException("Account doesn't exist!"));
    }
}
