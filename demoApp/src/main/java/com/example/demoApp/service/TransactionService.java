package com.example.demoApp.service;

import com.example.demoApp.model.TransactionType;
import com.example.demoApp.service.dto.TransactionDTO;

import java.util.List;


public interface TransactionService {

    TransactionDTO getTransaction(int transactionId);

    List<TransactionDTO> getAllTransactions();

    /**
     * Persist a new transaction.
     * The transaction can have only an origin account ({@link TransactionType#WITHDRAWAL}), destination account
     * ({@link TransactionType#DEPOSIT}) or both ({@link TransactionType#TRANSFER}).
     * Both account mustn't be BLOCKED/CLOSED in order for the transaction to succeeds, and the origin account must
     * have enough funds for the transaction. (failure of meeting this requirement for
     * {@link TransactionType#TRANSFER} transactions would result in account being limited)
     *
     * @param transactionDTO The new transaction DTO
     * @return The created transaction instance
     */
    TransactionDTO addTransaction(TransactionDTO transactionDTO);
}
