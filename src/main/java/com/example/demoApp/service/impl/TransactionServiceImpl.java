package com.example.demoApp.service.impl;

import com.example.demoApp.exception.ApiError;
import com.example.demoApp.exception.ValidationException;
import com.example.demoApp.model.Account;
import com.example.demoApp.model.Transaction;
import com.example.demoApp.model.TransactionType;
import com.example.demoApp.repository.TransactionRepository;
import com.example.demoApp.service.*;
import com.example.demoApp.service.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demoApp.model.TransactionType.DEPOSIT;
import static com.example.demoApp.model.TransactionType.TRANSFER;
import static com.example.demoApp.model.TransactionType.WITHDRAWAL;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final String VALIDATION_ERROR = "Validation Error";

    @Autowired
    private AccountService accountService;

    @Autowired
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
        List<ApiError> validationErrors;
        Account originAccount = getAccount(transactionDTO.originAccount);
        Account destinationAccount = getAccount(transactionDTO.destinationAccount);

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

    private Account getAccount(Integer AccountId) {
        try {
            return AccountId == null ? null : accountService.getAccountEntity(AccountId);
        } catch (EntityNotFoundException e) {
            throw new InvalidEntityReferenceException(Account.class, AccountId);
        }
    }

    private List<ApiError> validateTransaction(TransactionDTO transactionDTO, Account origin, Account destination) {
        List<ApiError> validationErrors = new ArrayList<>();

        if (transactionDTO.transactionType == TRANSFER && origin != null && origin.getId().equals(destination.getId())) {
            validationErrors.add(new ApiError(VALIDATION_ERROR, "Cannot transfer to same account"));
        }

        if (transactionDTO.amount <= 0) {
            validationErrors.add(new ApiError(VALIDATION_ERROR, "Transaction amount must be a positive number"));
        }

        if ((origin == null) == transactionDTO.transactionType.shouldHaveOriginAccount) {
            validationErrors.add(new ApiError(VALIDATION_ERROR, transactionDTO.transactionType.name() +
                    " transaction " + (origin == null ? "must" : "mustn't") + " have an origin account"));
        }

        if ((destination == null) == transactionDTO.transactionType.shouldHaveDestinationAccount) {
            validationErrors.add(new ApiError(VALIDATION_ERROR, transactionDTO.transactionType.name() +
                    " transaction " + (origin == null ? "must" : "mustn't") + " have a destination account"));
        }

        if (transactionDTO.transactionType != DEPOSIT && origin != null
                && !origin.getStatus().canBeOriginOf(transactionDTO.transactionType)) {
            validationErrors.add(new ApiError(VALIDATION_ERROR, "Can't move money from origin account." +
                    "(status: " + origin.getStatus() + ")"));
        }

        if (transactionDTO.transactionType != WITHDRAWAL && destination != null
                && !destination.getStatus().canBeDestinationOf(transactionDTO.transactionType)) {
            validationErrors.add(new ApiError(VALIDATION_ERROR, "Can't move money to destination account. " +
                    "(status: " + destination.getStatus() + ")"));
        }

        return validationErrors;
    }

    private Transaction getTransactionEntity(int transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(() -> new EntityNotFoundException
                (Transaction.class, transactionId));
    }
}
