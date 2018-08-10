package com.example.demoApp.service.impl;

import com.example.demoApp.exception.ApiError;
import com.example.demoApp.service.EntityNotFoundException;
import com.example.demoApp.service.InsufficientFunds;
import com.example.demoApp.exception.ValidationException;
import com.example.demoApp.model.Account;
import com.example.demoApp.model.Transaction;
import com.example.demoApp.repository.AccountRepository;
import com.example.demoApp.repository.TransactionRepository;
import com.example.demoApp.service.*;
import com.example.demoApp.service.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demoApp.model.TransactionType.*;

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
        List<ApiError> validationErrors;

        if (transactionDTO.originAccount != null) {
            originAccount = accountRepository.findById(transactionDTO.originAccount).get();
        }

        if (transactionDTO.destinationAccount != null) {
            destinationAccount = accountRepository.findById(transactionDTO.destinationAccount).get();
        }

        validationErrors = validateTransaction(transactionDTO, originAccount, destinationAccount);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
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
        transaction.setTransactionType(transactionDTO.transactionType);
        transaction.setAmount(transactionDTO.amount);
        transaction.setComments(transactionDTO.comments);
        return new TransactionDTO(transactionRepository.save(transaction));
    }

    private List<ApiError> validateTransaction(TransactionDTO transactionDTO, Account origin, Account destination) {
        List<ApiError> validationErrors = new ArrayList<>();

        if (transactionDTO.amount <= 0) {
            validationErrors.add(new ApiError("Invalid amount", "Transaction amount must be a positive number"));
        }

        if ((origin == null) == transactionDTO.transactionType.shouldHaveOriginAccount) {
            validationErrors.add(new ApiError("Invalid Transaction", transactionDTO.transactionType.name() +
                    " transaction " + (origin == null ? "must" : "mustn't") + " have an origin account"));
        }

        if ((destination == null) == transactionDTO.transactionType.shouldHaveDestinationAccount) {
            validationErrors.add(new ApiError("Invalid Transaction", transactionDTO.transactionType.name() +
                    " transaction " + (origin == null ? "must" : "mustn't") + " have a destination account"));
        }

        if (origin != null && !origin.getStatus().canBeOriginOf(transactionDTO.transactionType)) {
            validationErrors.add(new ApiError("Invalid transaction", "Can't move money from origin account." +
                    "(status: " + origin.getStatus() + ")"));
        }

        if (destination != null && !destination.getStatus().canBeDestinationOf(transactionDTO.transactionType)) {
            validationErrors.add(new ApiError("Invalid transaction", "Can't move money to destination account. " +
                    "(status: " + destination.getStatus() + ")"));
        }

        return validationErrors;
    }

    private Transaction getTransactionEntity(int transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new EntityNotFoundException
                (Transaction.class, transactionId));
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
