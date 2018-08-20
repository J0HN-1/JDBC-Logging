package com.example.demoApp.service.dto;

import com.example.demoApp.DemoAppApplication;
import com.example.demoApp.model.Transaction;
import com.example.demoApp.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static java.util.Objects.*;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class TransactionDTO {

    @JsonProperty(access = READ_ONLY)
    public Integer id;

    public Integer originAccount;

    public Integer destinationAccount;

    @NotNull
    public TransactionType transactionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DemoAppApplication.DATE_FORMAT, timezone = "UTC")
    public Date date;

    @NotNull
    @Min(0)
    public Double amount;

    @NotBlank
    public String comments;

    public TransactionDTO() {
    }

    public TransactionDTO(Integer originAccount, Integer destinationAccount, TransactionType transactionType,
                          double amount, String comments) {
        this(null, originAccount, destinationAccount, transactionType, null, amount, comments);
    }

    public TransactionDTO(Integer id, Integer originAccount, Integer destinationAccount, @NotNull TransactionType
            transactionType, Date date, @NotNull @Min(0) double amount, @NotBlank String comments) {
        this.id = id;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.transactionType = transactionType;
        this.date = date;
        this.amount = amount;
        this.comments = comments;
    }

    public TransactionDTO(Transaction transaction) {
        id = transaction.getId();
        originAccount = isNull(transaction.getOriginAccount()) ? null : transaction.getOriginAccount().getId();
        destinationAccount = isNull(transaction.getDestinationAccount()) ? null : transaction.getDestinationAccount().getId();
        transactionType = transaction.getTransactionType();
        date = transaction.getDate();
        amount = transaction.getAmount();
        comments = transaction.getComments();
    }
}
