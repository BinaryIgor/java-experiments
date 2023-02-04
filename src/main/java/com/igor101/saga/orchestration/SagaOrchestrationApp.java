package com.igor101.saga.orchestration;

import com.igor101.events.InMemoryEvents;
import com.igor101.saga.orchestration.ads.*;

import java.util.Set;
import java.util.UUID;

public class SagaOrchestrationApp {
    public static void main(String[] args) {
        var events = new InMemoryEvents();
        var eventPublisher = events.publisher();

        var accountService = new AccountService(events);
        var accountInventoryService = new AccountInventoryService(events);

        var accountIdState = new SagaState<UUID>();
        var createAccountStep = new SagaStep<>(
                "CreateAccount",
                state -> {
                    var account = new Account(UUID.randomUUID(), "Some account");
                    state.set(account.id());
                    accountService.createAccount(account);
                },
                state -> {
                    state.get().ifPresent(accountService::deleteAccount);
                },
                AccountCreated.class,
                AccountRejected.class,
                accountIdState);

        var createInventoryStep = new SagaStep<>(
                "CreateInventory",
                accountId -> {
                    accountInventoryService.create(new AccountInventory(accountId.getOrThrow(), Set.of()));
                },
                accountId -> {
                    System.out.println("Rejection not needed!");
                },
                AccountInventoryCreated.class,
                AccountInventoryRejected.class,
                accountIdState);

        new SagaDefinition(events)
                .addStep(createAccountStep)
                .addStep(createInventoryStep)
                .execute();


        var accountId = accountIdState.getOrThrow();
        var account = accountService.ofId(accountId);
        var inventory = accountInventoryService.ofAccountId(accountId);

        System.out.println();
        System.out.println(account);
        System.out.println(inventory);
    }
}
