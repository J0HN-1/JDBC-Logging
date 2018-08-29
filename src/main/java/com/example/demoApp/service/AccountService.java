package com.example.demoApp.service;

import com.example.demoApp.model.Account;
import com.example.demoApp.model.AccountStatus;
import com.example.demoApp.model.AccountType;
import com.example.demoApp.service.dto.AccountDTO;

import java.util.List;

public interface AccountService {

    AccountDTO getAccount(int accountId);

    List<AccountDTO> getAllAccounts();

    AccountDTO createAccount(AccountDTO accountDTO);

    void setAccountType(int accountId, AccountType type);

    void setAccountStatus(int accountId, AccountStatus status);

    void markAccountAsLimited(int accountId);

    Account getAccountEntity(int accountId);
}
