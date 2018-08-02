package com.example.demoApp.service.dto;

import com.example.demoApp.model.Transaction;
import com.example.demoApp.model.TransactionType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class TransactionDTO {

    public Integer originAccount;

    public Integer destinationAccount;

    @NotNull
    public TransactionType transactionType;

    public Date date;

    @NotNull
    @Min(0)
    public double amount;

    @NotNull
    public String comments;

    public TransactionDTO() {
    }

    public TransactionDTO(Integer originAccount, Integer destinationAccount, TransactionType transactionType,
                          double amount, String comments) {
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.comments = comments;
    }

    public TransactionDTO(Transaction transaction) {
        originAccount = transaction.getOriginAccount().getId();
        destinationAccount = transaction.getDestinationAccount().getId();
        transactionType = transaction.getTransactionType();
        date = transaction.getDate();
        amount = transaction.getAmount();
        comments = transaction.getComments();
    }
}
