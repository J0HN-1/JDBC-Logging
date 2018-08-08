package com.example.demoApp.service.dto;

import com.example.demoApp.model.Account;
import com.example.demoApp.model.AccountStatus;
import com.example.demoApp.model.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.validation.constraints.NotNull;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.*;

import static com.example.demoApp.service.dto.View.*;

public class AccountDTO {

    @JsonProperty(access = READ_ONLY)
    public Integer id;

    @NotNull
    public Integer ownerId;

    @JsonView(AccountTypeView.class)
    @NotNull
    public AccountType type;

    @JsonView(AccountStatusView.class)
    @NotNull
    public AccountStatus status;

    @JsonProperty(access = READ_ONLY)
    public double balance;

    public AccountDTO() {
    }

    public AccountDTO(Integer id, Integer ownerId, AccountType type, AccountStatus status) {
        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.status = status;
    }

    public AccountDTO(Integer id, Integer ownerId, AccountType type, AccountStatus status, double balance) {
        this(id, ownerId, type, status);
        this.balance = balance;
    }

    public AccountDTO(Account account) {
        id = account.getId();
        ownerId = account.getOwner().getId();
        type = account.getType();
        status = account.getStatus();
    }
}
