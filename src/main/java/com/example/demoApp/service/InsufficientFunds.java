package com.example.demoApp.service;

import com.example.demoApp.model.Account;

public class InsufficientFunds extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InsufficientFunds(Account account, double requestedAmount) {
        super("Not enough money in account " + account.getId() + ". Account funds: " + account.getBalance() + " (Requested: " + requestedAmount +")");
    }
}
