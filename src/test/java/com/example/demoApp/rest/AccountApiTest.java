package com.example.demoApp.rest;

import com.example.demoApp.controller.AccountController;
import com.example.demoApp.model.Account;
import com.example.demoApp.model.AccountStatus;
import com.example.demoApp.model.AccountType;
import com.example.demoApp.model.User;
import com.example.demoApp.repository.AccountRepository;
import com.example.demoApp.repository.UserRepository;
import com.example.demoApp.service.AccountService;
import com.example.demoApp.service.dto.AccountDTO;
import com.example.demoApp.util.MapBuilder;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.Optional;

import static com.example.demoApp.DemoAppApplication.REST_API_PREFIX;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@WebMvcTest(controllers = AccountController.class, secure = false)
public class AccountApiTest extends RestApiTestBase {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AccountRepository accountRepository;

    @SpyBean
    private AccountService accountService;

    @Test
    public void getExistingAccount() {
        doReturn(new AccountDTO(1, 1, AccountType.PRIVATE, AccountStatus.OPEN)).when(accountService).getAccount(1);

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/accounts/{id}", 1)
        .then()
            .statusCode(SC_OK)
            .body("id", equalTo(1),
                    "ownerId", is(1),
                    "type", is("PRIVATE"),
                    "status", is("OPEN"));
    }

    @Test
    public void getNonExistingAccount() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/accounts/{id}", 1)
        .then()
            .statusCode(SC_NOT_FOUND)
            .body("errors.size()", is(1),
                    "errors[0].title", is("Entity Not Found"),
                    "errors[0].detail", is("Cannot find entity Account with ID " + 1));
    }

    @Test
    public void getAllAccounts() {
        doReturn(Arrays.asList(
                new AccountDTO(1, 2, AccountType.CORPORATE, AccountStatus.CLOSED),
                new AccountDTO(2, 1, AccountType.BUSINESS, AccountStatus.LIMITED)))
            .when(accountService).getAllAccounts();

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/accounts")
        .then()
            .statusCode(SC_OK)
            .body("size()", is(2),
                    "[0].id", equalTo(1),
                    "[0].ownerId", is(2),
                    "[0].type", is("CORPORATE"),
                    "[0].status", is("CLOSED"),
                    "[1].id", is(2),
                    "[1].ownerId", is(1),
                    "[1].type", is("BUSINESS"),
                    "[1].status", is("LIMITED"));
    }

    @Test
    public void createAccount() {
        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1, "text", "text", "text", "text")));
        when(accountRepository.save(any())).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1);
            account.setBalance(0.0);
            return account;
        });

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("ownerId", 1)
                    .and("type", "PRIVATE")
                    .and("status", "OPEN")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/accounts")
        .then()
            .statusCode(SC_CREATED)
            .header(LOCATION, not(empty()))
            .body("id", is(1),
                    "ownerId", is(1),
                    "type", is("PRIVATE"),
                    "status", is("OPEN"),
                    "balance", is(0.0f));
    }

    @Test
    public void accountValidation() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("ownerId", 1)
                    .and("status", null)
                    .build())
        .when()
            .post(REST_API_PREFIX + "/accounts")
        .then()
            .statusCode(SC_BAD_REQUEST)
            .body("errors.size()", is(2),
                "errors[0].title", is("Field Error"),
                "errors[1].title", is("Field Error"),
                "errors.detail", containsInAnyOrder(Arrays.asList(containsString("type"), containsString("status"))));
    }

    @Test
    public void unknownAccountOwner() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("ownerId", 1)
                    .and("type", "PRIVATE")
                    .and("status", "OPEN")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/accounts")
        .then()
            .statusCode(SC_UNPROCESSABLE_ENTITY)
            .body("errors.size()", is(1),
                    "errors[0].title", is("Invalid Entity Reference"),
                    "errors[0].detail", is("Invalid reference to User with ID " + 1));
    }

    @Test
    public void changeAccountType() {
        doNothing().when(accountService).setAccountType(anyInt(), any());

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.from("type", "CORPORATE").build())
        .when()
            .put(REST_API_PREFIX + "/accounts/{id}/type", 1)
        .then()
            .statusCode(SC_SEE_OTHER)
            .header(LOCATION, Matchers.endsWith(REST_API_PREFIX + "/accounts/1"));

        verify(accountService, times(1)).setAccountType(1, AccountType.CORPORATE);
    }

    @Test
    public void changeAccountStatus() {
        doNothing().when(accountService).setAccountStatus(anyInt(), any());

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.from("status", AccountStatus.CLOSED).build())
        .when()
            .put(REST_API_PREFIX + "/accounts/{id}/status", 1)
        .then()
            .statusCode(SC_SEE_OTHER)
            .header(LOCATION, Matchers.endsWith(REST_API_PREFIX + "/accounts/1"));

        verify(accountService, times(1)).setAccountStatus(1, AccountStatus.CLOSED);
    }
}
