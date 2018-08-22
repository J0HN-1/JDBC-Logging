package com.example.demoApp.model;

public enum TransactionType {
    DEPOSIT(false, true),
    WITHDRAWAL(true, false),
    TRANSFER(true, true);

    public final boolean shouldHaveOriginAccount;
    public final boolean shouldHaveDestinationAccount;

    TransactionType(boolean shouldHaveOriginAccount, boolean shouldHaveDestinationAccount) {
        this.shouldHaveOriginAccount = shouldHaveOriginAccount;
        this.shouldHaveDestinationAccount = shouldHaveDestinationAccount;
    }
}
