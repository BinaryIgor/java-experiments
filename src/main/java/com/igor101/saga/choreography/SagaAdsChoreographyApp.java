package com.igor101.saga.choreography;

import com.igor101.events.InMemoryEvents;
import com.igor101.saga.choreography.ads.AccountInventory;
import com.igor101.saga.choreography.ads.AccountService;
import com.igor101.saga.choreography.ads.CreateAccount;
import com.igor101.saga.choreography.ads.InventoryService;

import java.util.Set;

public class SagaAdsChoreographyApp {
    public static void main(String[] args) {
        var events = new InMemoryEvents();

        var accountService = new AccountService(events);
        var inventoryService = new InventoryService(events);

        var account = accountService.create(new CreateAccount("some account",
                new AccountInventory(Set.of("Nike", "Puma"))));

        System.out.println(account);
        System.out.println(accountService.getById(account.id()));
        System.out.println(inventoryService.getById(account.id()));

        var account2 = accountService.create(new CreateAccount("some name", new AccountInventory(null)));
        System.out.println(accountService.getById(account2.id()));
    }
}
