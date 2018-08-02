package com.example.demoApp.unit;

import com.example.demoApp.model.Transaction;
import com.example.demoApp.model.TransactionType;
import com.example.demoApp.repository.TransactionRepository;
import com.example.demoApp.service.TransactionService;
import com.example.demoApp.service.dto.TransactionDTO;
import com.example.demoApp.util.CleanDatabaseRule;
import com.example.demoApp.util.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests for Transaction entity & service.
 * Please note these test are writing fixture to the DB and
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ServiceTestsConfiguration.class)
public class TransactionServiceTests {

    @Rule
    @Autowired
    public CleanDatabaseRule cleanDatabaseRule = new CleanDatabaseRule();

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Test
    @Sql({"classpath:db/sql/dummy_user.sql", "classpath:db/sql/dummy_account.sql", "classpath:db/sql/dummy_transaction.sql"})
    public void testTransactionsAreReadOnly() {
        Transaction transaction = transactionRepository.findById(1).get();
        transaction.setComments("new comment");
        TestUtils.checkExceptionOfTypeIsThrownWhenExecuting(transactionRepository::save, transaction,
                UnsupportedOperationException.class);
    }

    @Test
    @Sql({"classpath:db/sql/dummy_user.sql", "classpath:db/sql/dummy_account.sql"})
    public void testTransactionsWithNegativeAmount() {
        TransactionDTO transactionDTO = new TransactionDTO(3, 2, TransactionType.TRANSFER, -500, "whatever");
        TestUtils.checkExceptionOfTypeIsThrownWhenExecuting(transactionService::addTransaction, transactionDTO,
                IllegalArgumentException.class);
    }

}
