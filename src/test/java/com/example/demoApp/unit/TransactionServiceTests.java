package com.example.demoApp.unit;

import com.example.demoApp.exception.ValidationException;
import com.example.demoApp.model.Transaction;
import com.example.demoApp.model.TransactionType;
import com.example.demoApp.repository.TransactionRepository;
import com.example.demoApp.service.EntityNotFoundException;
import com.example.demoApp.service.TransactionService;
import com.example.demoApp.service.dto.TransactionDTO;
import com.example.demoApp.util.CleanDatabaseRule;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

/**
 * Tests for Transaction entity & service.
 * Please note these test are writing fixture to the DB and
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ServiceTestsConfiguration.class)
@Transactional
public class TransactionServiceTests {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Test(expected = UnsupportedOperationException.class)
    @Sql({"classpath:db/sql/dummy_user.sql", "classpath:db/sql/dummy_account.sql", "classpath:db/sql/dummy_transaction.sql"})
    public void transactionsAreReadOnly() {
        Transaction transaction = transactionRepository.findAll().get(0);
        transaction.setComments("new comment");
        transactionRepository.save(transaction);
        transactionRepository.flush();
    }

    @Test(expected = ValidationException.class)
    @Sql({"classpath:db/sql/dummy_user.sql", "classpath:db/sql/dummy_account.sql"})
    public void transactionsWithNegativeAmount() {
        TransactionDTO transactionDTO = new TransactionDTO(3, 2, TransactionType.TRANSFER, -500, "whatever");
        transactionService.addTransaction(transactionDTO);
    }

    @Test(expected = ValidationException.class)
    @Sql({"classpath:db/sql/dummy_user.sql", "classpath:db/sql/dummy_account.sql"})
    public void transactionsWithZeroAmount() {
        TransactionDTO transactionDTO = new TransactionDTO(3, 2, TransactionType.TRANSFER, 0, "whatever");
        transactionService.addTransaction(transactionDTO);
    }

    @Test(expected = EntityNotFoundException.class)
    @Sql({"classpath:db/sql/dummy_user.sql", "classpath:db/sql/dummy_account.sql", "classpath:db/sql/dummy_transaction.sql"})
    public void nonExistingTransaction() {
        try {
            transactionService.getTransaction(9);
        } catch (Exception e) {
            throw (RuntimeException) (e.getCause() == null ? e : ExceptionUtils.getRootCause(e));
        }
    }

}
