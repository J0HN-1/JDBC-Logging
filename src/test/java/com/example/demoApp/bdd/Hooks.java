package com.example.demoApp.bdd;

import com.example.demoApp.unit.ServiceTestsConfiguration;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@ContextConfiguration(classes = ServiceTestsConfiguration.class)
public class Hooks {

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionStatus transactionStatus;

    @Before
    public void startTransaction() {
        transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @After
    public void cleanDB() {
        transactionManager.rollback(transactionStatus);
    }
}
