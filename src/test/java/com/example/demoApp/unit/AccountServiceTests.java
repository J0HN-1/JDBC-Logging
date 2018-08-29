package com.example.demoApp.unit;


import com.example.demoApp.model.AccountStatus;
import com.example.demoApp.model.AccountType;
import com.example.demoApp.repository.AccountRepository;
import com.example.demoApp.service.AccountService;
import com.example.demoApp.service.dto.AccountDTO;
import com.example.demoApp.util.CleanDatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ServiceTestsConfiguration.class)
@Transactional
public class AccountServiceTests {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Test(expected = Exception.class)
    public void accountMustHaveOwner() {
        accountService.createAccount(new AccountDTO(null, 1, AccountType.PRIVATE, AccountStatus.OPEN));
    }

    @Sql("/db/sql/dummy_user.sql")
    @Test(expected = ConstraintViolationException.class)
    public void accountMustHaveType() {
        accountService.createAccount(new AccountDTO(null, 1, null, AccountStatus.OPEN));
    }

    @Sql("/db/sql/dummy_user.sql")
    @Test(expected = ConstraintViolationException.class)
    public void accountMustHaveStatus() {
        accountService.createAccount(new AccountDTO(null, 1, AccountType.PRIVATE, null));
    }

    @Sql("/db/sql/dummy_user.sql")
    public void createAccount() {
        AccountDTO account = accountService.createAccount(new AccountDTO(null, 1, AccountType.PRIVATE, AccountStatus.OPEN));
        assertThat(account.id, is(1));
        assertThat(account.ownerId, is(1));
        assertThat(account.status, is(AccountStatus.OPEN));
        assertThat(account.type, is(AccountType.PRIVATE));
        assertThat(account.balance, is(0.0));
    }

    @Test
    @Sql({"classpath:db/sql/dummy_user.sql", "classpath:db/sql/dummy_account.sql"})
    public void accountStatusChange() {
        accountService.setAccountStatus(1, AccountStatus.CLOSED);
        assertThat("Account status wasn't changed", accountRepository.findById(1).get().getStatus(), is(AccountStatus.CLOSED));
    }

    @Test
    @Sql({"classpath:db/sql/dummy_user.sql", "classpath:db/sql/dummy_account.sql"})
    public void accountTypeChange() {
        accountService.setAccountType(1, AccountType.BUSINESS);
        assertThat("Account type wasn't changed", accountRepository.findById(1).get().getType(), is(AccountType.BUSINESS));
    }
}
