package com.example.demoApp.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "from_account")
    private Account originAccount;

    @ManyToOne
    @JoinColumn(name = "to_account")
    private Account destinationAccount;

    @NotNull
    @Enumerated(STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @NotNull
    @Column
    private double amount = 0.0;

    @NotNull
    @Temporal(TIMESTAMP)
    @Column(name = "transaction_date", nullable = false)
    private Date date;

    @NotBlank
    @Column(nullable = false)
    private String comments;

    @PrePersist
    private void persist() {
        date = new Date();
    }

    @PreUpdate
    private void update() {
        throw new UnsupportedOperationException("Transaction cannot be changed after it's committed");
    }

    public Transaction() {
    }

    public Transaction(Integer id, Account originAccount, Account destinationAccount, @NotNull TransactionType
            transactionType, @NotNull double amount, @NotNull Date date, @NotBlank String comments) {
        this.id = id;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
        this.comments = comments;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(Account originAccount) {
        this.originAccount = originAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
