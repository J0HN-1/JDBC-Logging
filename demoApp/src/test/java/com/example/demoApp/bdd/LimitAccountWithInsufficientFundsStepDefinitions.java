package com.example.demoApp.bdd;

import com.example.demoApp.model.AccountStatus;
import com.example.demoApp.model.AccountType;
import com.example.demoApp.model.TransactionType;
import com.example.demoApp.service.AccountService;
import com.example.demoApp.service.TransactionService;
import com.example.demoApp.service.UserService;
import com.example.demoApp.service.dto.AccountDTO;
import com.example.demoApp.service.dto.TransactionDTO;
import com.example.demoApp.service.dto.UserDTO;
import com.example.demoApp.unit.ServiceTestsConfiguration;
import com.example.demoApp.util.TestUtils;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ContextConfiguration(classes = ServiceTestsConfiguration.class)
public class LimitAccountWithInsufficientFundsStepDefinitions {
    private static final int ORIGIN_ACCOUNT_ID = 1;
    private static final int DESTINATION_ACCOUNT_ID = 2;

    private int originAccountBalance;

    private Exception expectedException;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserService UserService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;


    @Given("^an origin account with (.*) status and (\\d+) in its balance$")
    public void origin_account_open_exists_with_balance(AccountStatus status, int balance) {
        UserService.saveUser(new UserDTO(1, "John", "Doe", "some street", "some city"));
        accountService.createAccount(new AccountDTO(ORIGIN_ACCOUNT_ID, 1, AccountType.values()[new Random().nextInt(AccountType.values().length)], status, originAccountBalance = balance));
    }

    @Given("^a destination account with (.*) status$")
    public void destination_open_account_exists(AccountStatus status) {
        UserService.saveUser(new UserDTO(2, "Jane", "Doe", "some street", "some city"));
        accountService.createAccount(new AccountDTO(DESTINATION_ACCOUNT_ID, 2, AccountType.values()[new Random().nextInt(AccountType.values().length)], status));
    }

    @When("^a transaction of type (.*) is made between them with an amount bigger than the origin balance$")
    public void transfer_transaction_with_larger_amount_than_origin_is_added(TransactionType transactionType) {
        try {
            transactionService.addTransaction(new TransactionDTO(ORIGIN_ACCOUNT_ID, DESTINATION_ACCOUNT_ID, transactionType, originAccountBalance + .5, "test transfer"));
        } catch (Exception e) {
            expectedException = e;
        }
    }

    @Then("^the transaction mustn't be saved$")
    public void the_transaction_isnt_saved() {
        assertThat("The transaction shouldn't be saved", transactionService.getAllTransactions().size(), is(0));
    }

    @Then("^an (.*) error should be raised$")
    public void expected_error_is_raised(String exceptionClass) {
        assertThat(String.format("An excption of type %s should be thrown", exceptionClass), expectedException.getClass().getSimpleName(), is(exceptionClass));
    }

    @Then("^the origin account status should be (.*)")
    public void the_origin_account_status_have_changed(AccountStatus status) {
        assertThat(accountService.getAccount(ORIGIN_ACCOUNT_ID).status, is(status));
    }

    @After
    public void cleanDB() {
        TestUtils.cleanDB(dataSource);
    }
}

