package com.example.demoApp.service.impl;

import com.example.demoApp.model.Account;
import com.example.demoApp.model.Transaction;
import com.example.demoApp.repository.AccountRepository;
import com.example.demoApp.repository.TransactionRepository;
import com.example.demoApp.service.*;
import com.example.demoApp.service.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demoApp.model.TransactionType.*;
import static com.example.demoApp.model.AccountStatus.*;
import static javax.transaction.Transactional.TxType.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private AccountService accountService;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    @Override
    public TransactionDTO getTransaction(int transactionId) {
        return new TransactionDTO(getTransactionEntity(transactionId));
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream().map(TransactionDTO::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TransactionDTO addTransaction(TransactionDTO transactionDTO) {
        Account originAccount = null;
        Account destinationAccount = null;

        if (transactionDTO.amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be a positive number");
        }

        if (transactionDTO.originAccount != null) {
            originAccount = accountRepository.findById(transactionDTO.originAccount)
                    .orElseThrow(() -> new NoSuchEntityException(Account.class, transactionDTO.originAccount));
        } else {
            if (transactionDTO.transactionType != DEPOSIT) {
                throw new InvalidTransaction("Missing origin account in transaction: " + transactionDTO);
            }
        }

        if (transactionDTO.destinationAccount != null) {
            destinationAccount = accountRepository.findById(transactionDTO.destinationAccount)
                    .orElseThrow(() -> new NoSuchEntityException(Account.class, transactionDTO.destinationAccount));
        } else {
            if (transactionDTO.transactionType != WITHDRAWAL) {
                throw new InvalidTransaction("Missing destination account in transaction: " + transactionDTO);
            }
        }

        if(originAccount != null && (originAccount.getStatus() == BLOCKED || originAccount.getStatus() == CLOSED)) {
            throw new InvalidTransaction("Invalid origin account. (status: " + originAccount.getStatus() + ")");
        }

        if(destinationAccount != null && (destinationAccount.getStatus() == BLOCKED || destinationAccount.getStatus() == CLOSED)) {
            throw new InvalidTransaction("Invalid destination account. (status: " + destinationAccount.getStatus() + ")");
        }

        if (originAccount != null && originAccount.getBalance() < transactionDTO.amount) {
            if (transactionDTO.transactionType == TRANSFER) {
                accountService.markAccountAsLimited(originAccount.getId());
            }
            throw new InsufficientFunds(originAccount, transactionDTO.amount);
        }

        if (originAccount != null) {
            originAccount.setBalance(originAccount.getBalance() - transactionDTO.amount);
        }

        if (destinationAccount != null) {
            destinationAccount.setBalance(destinationAccount.getBalance() + transactionDTO.amount);
        }

        Transaction transaction = new Transaction();
        transaction.setOriginAccount(originAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(transactionDTO.amount);
        return new TransactionDTO(transactionRepository.save(transaction));
    }

    private Transaction getTransactionEntity(int transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new NoSuchEntityException(Transaction.class, transactionId));
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
}
