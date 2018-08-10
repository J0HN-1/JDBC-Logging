package com.example.demoApp.service.impl;

import com.example.demoApp.model.Account;
import com.example.demoApp.model.AccountStatus;
import com.example.demoApp.model.AccountType;
import com.example.demoApp.model.User;
import com.example.demoApp.repository.AccountRepository;
import com.example.demoApp.repository.UserRepository;
import com.example.demoApp.service.AccountService;
import com.example.demoApp.service.EntityNotFoundException;
import com.example.demoApp.service.InvalidEntityReference;
import com.example.demoApp.service.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demoApp.model.AccountStatus.LIMITED;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Transactional
@Service
public class AccountServiceImpl implements AccountService {

    private UserRepository userRepository;
    private AccountRepository accountRepository;

    @Override
    public AccountDTO getAccount(int accountId) {
        return new AccountDTO(getAccountEntity(accountId));
    }

    @Override
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(Collectors.toList());
    }

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setOwner(userRepository.findById(accountDTO.ownerId).orElseThrow(() -> new InvalidEntityReference(User.class, accountDTO.ownerId)));
        account.setType(accountDTO.type);
        account.setStatus(accountDTO.status);
        account.setBalance(accountDTO.balance);
        return new AccountDTO(accountRepository.save(account));
    }

    @Override
    public void setAccountType(int accountId, AccountType type) {
        Account account = getAccountEntity(accountId);
        account.setType(type);
        accountRepository.save(account);
    }

    @Override
    public void setAccountStatus(int accountId, AccountStatus status) {
        Account account = getAccountEntity(accountId);
        account.setStatus(status);
        accountRepository.save(account);
    }

    @Transactional(REQUIRES_NEW)
    @Override
    public void markAccountAsLimited(int accountId) {
        setAccountStatus(accountId, LIMITED);
    }

    private Account getAccountEntity(int accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new EntityNotFoundException(Account.class, accountId));
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
