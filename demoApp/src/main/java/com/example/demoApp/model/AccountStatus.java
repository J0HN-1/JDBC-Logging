package com.example.demoApp.model;

import java.util.EnumSet;

import static com.example.demoApp.model.TransactionType.*;

public enum AccountStatus {
    OPEN(EnumSet.of(WITHDRAWAL, TRANSFER), EnumSet.of(DEPOSIT, TRANSFER)),
    CLOSED(EnumSet.noneOf(TransactionType.class), EnumSet.noneOf(TransactionType.class)),
    LIMITED(EnumSet.noneOf(TransactionType.class), EnumSet.of(DEPOSIT)),
    BLOCKED(EnumSet.noneOf(TransactionType.class), EnumSet.noneOf(TransactionType.class));

    private final EnumSet<TransactionType> allowedTransactionAsOrigin;
    private final EnumSet<TransactionType> allowedTransactionAsDestination;

    AccountStatus(EnumSet<TransactionType> allowedTransactionAsOrigin, EnumSet<TransactionType>
            allowedTransactionAsDestination) {
        this.allowedTransactionAsOrigin = allowedTransactionAsOrigin;
        this.allowedTransactionAsDestination = allowedTransactionAsDestination;
    }

    public boolean canBeOriginOf(TransactionType type) {
        return allowedTransactionAsOrigin.contains(type);
    }

    public boolean canBeDestinationOf(TransactionType type) {
        return allowedTransactionAsDestination.contains(type);
    }
}
