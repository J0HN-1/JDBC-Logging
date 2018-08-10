package com.example.demoApp.bdd;

import com.example.demoApp.model.TransactionType;
import com.example.demoApp.service.TransactionService;
import com.example.demoApp.service.dto.TransactionDTO;
import com.example.demoApp.unit.ServiceTestsConfiguration;
import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;

import static com.example.demoApp.bdd.AccountSettingsStepDefinitions.DESTINATION_ACCOUNT_ID;
import static com.example.demoApp.bdd.AccountSettingsStepDefinitions.ORIGIN_ACCOUNT_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static com.example.demoApp.model.TransactionType.*;
import static java.util.Objects.*;

//@ContextConfiguration(classes = ServiceTestsConfiguration.class)
public class TransactionsStepDefinitions {

    private Integer originAccount;
    private Integer destinationAccount;
    private double transactionAmount;
    private Integer savedTransactionId;

    private Exception expectedException;

    @Autowired
    private TransactionService transactionService;

    @When("^a transaction of type (TRANSFER|DEPOSIT|WITHDRAWAL) is made with the amount of ([0-9.]+)$")
    public void a_transaction_of_type_and_amount_is_made(TransactionType transactionType, double amount) {
        try {
            originAccount = transactionType == DEPOSIT ? null : ORIGIN_ACCOUNT_ID;
            destinationAccount = transactionType == WITHDRAWAL ? null : DESTINATION_ACCOUNT_ID;
            TransactionDTO transactionDTO = transactionService.addTransaction(new TransactionDTO(originAccount, destinationAccount, transactionType, transactionAmount = amount,"test transfer"));
            savedTransactionId = transactionDTO.id;
        } catch (Exception e) {
            expectedException = e;
        }
    }

    @Then("^the transaction must(n't)? be saved$")
    public void the_transaction_is_saved(String negation) {
        if (isNull(negation)) {
            TransactionDTO transaction = transactionService.getTransaction(savedTransactionId);
            assertThat(transaction.date, is(notNullValue()));
            assertThat(transaction.originAccount, is(originAccount));
            assertThat(transaction.destinationAccount, is(destinationAccount));
            assertThat(transaction.amount, is(transactionAmount));
        } else {
            assertThat(transactionService.getAllTransactions().size(), is(0));
        }
    }

    @Then("^an exception of type (.*) should be thrown")
    public void expected_error_is_raised(String exceptionClass) {
        assertThat(String.format("An excption of type %s should be thrown", exceptionClass), expectedException.getClass().getSimpleName(), is(exceptionClass));
    }
}

