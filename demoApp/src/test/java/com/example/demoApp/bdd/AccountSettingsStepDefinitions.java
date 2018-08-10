package com.example.demoApp.bdd;

import com.example.demoApp.model.AccountStatus;
import com.example.demoApp.model.AccountType;
import com.example.demoApp.service.AccountService;
import com.example.demoApp.service.UserService;
import com.example.demoApp.service.dto.AccountDTO;
import com.example.demoApp.service.dto.UserDTO;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AccountSettingsStepDefinitions {
    static Integer ORIGIN_ACCOUNT_ID = 1;
    static Integer DESTINATION_ACCOUNT_ID = 2;

    @Autowired
    private UserService UserService;

    @Autowired
    private AccountService accountService;

    @Given("^an origin account with (.*) status(?: and account type (.*))?(?: and ([0-9.]+) in its balance)?$")
    public void origin_account_open_exists_with_balance(AccountStatus status, AccountType type, Double balance) {
        double accountBalance = balance == null ? 0 : balance;
        AccountType accountType = type == null ? AccountType.PRIVATE : type;
        UserDTO user = UserService.saveUser(new UserDTO(1, "John", "Doe", "some street", "some city"));
        AccountDTO account = new AccountDTO(null, user.id, accountType, status, accountBalance);
        ORIGIN_ACCOUNT_ID = accountService.createAccount(account).id;
    }

    @Given("^a destination account with (.*) status(?: and account type (.*))?(?: and ([0-9.]+) in its balance)?$")
    public void destination_open_account_exists(AccountStatus status, AccountType type, Double balance) {
        double accountBalance = balance == null ? 0 : balance;
        AccountType accountType = type == null ? AccountType.PRIVATE : type;
        UserDTO user = UserService.saveUser(new UserDTO(2, "Jane", "Doe", "some street", "some city"));
        AccountDTO account = new AccountDTO(null, user.id, accountType, status, accountBalance);
        DESTINATION_ACCOUNT_ID = accountService.createAccount(account).id;
    }

    @Then("^the origin account status should be (.*)")
    public void the_origin_account_status_have_changed(AccountStatus status) {
        assertThat(accountService.getAccount(ORIGIN_ACCOUNT_ID).status, is(status));
    }

    @Then("^the (origin|destination) account should have ([0-9.]+) in its balance$")
    public void account_should_have_amount(String account, double amount) {
        int accountId = account.equals("origin") ? ORIGIN_ACCOUNT_ID : DESTINATION_ACCOUNT_ID;
        assertThat(accountService.getAccount(accountId).balance, is(amount));
    }
}
