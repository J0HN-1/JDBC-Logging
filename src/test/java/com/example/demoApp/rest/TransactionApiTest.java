package com.example.demoApp.rest;

import com.example.demoApp.DemoAppApplication;
import com.example.demoApp.controller.TransactionController;
import com.example.demoApp.model.Account;
import com.example.demoApp.model.AccountStatus;
import com.example.demoApp.model.AccountType;
import com.example.demoApp.model.Transaction;
import com.example.demoApp.repository.AccountRepository;
import com.example.demoApp.repository.TransactionRepository;
import com.example.demoApp.service.TransactionService;
import com.example.demoApp.service.dto.TransactionDTO;
import com.example.demoApp.util.MapBuilder;
import org.exparity.hamcrest.date.DateMatchers;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static com.example.demoApp.DemoAppApplication.REST_API_PREFIX;
import static com.example.demoApp.model.TransactionType.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@WebMvcTest(controllers = TransactionController.class, secure = false)
public class TransactionApiTest extends RestApiTestBase{

    private static DateTimeFormatter dtf;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private AccountRepository accountRepository;

    @SpyBean
    private TransactionService transactionService;

    @BeforeClass
    public static void initDateTimeFormatter() {
        dtf = DateTimeFormatter.ofPattern(DemoAppApplication.DATE_FORMAT);
    }

    @Test
    public void getTransaction() {
        ZonedDateTime zonedDate = ZonedDateTime.now(ZoneId.of("UTC"));
        doReturn(new TransactionDTO(1, 2, 3, TRANSFER, Date.from(zonedDate.toInstant()), 34, "comment"))
                .when(transactionService).getTransaction(1);

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/transactions/{id}", 1)
        .then()
            .statusCode(SC_OK)
            .body("id", is(1),
                    "originAccount", is(2),
                    "destinationAccount", is(3),
                    "transactionType", is("TRANSFER"),
                    "date", is(dtf.format(zonedDate)),
                    "amount",  is(34f),
                    "comments", is("comment"));
    }

    @Test
    public void getNonExistingTransaction() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/transactions/{id}", 1)
        .then()
            .statusCode(SC_NOT_FOUND);
    }

    @Test
    public void getAllUsers() {
        ZonedDateTime zonedDate1 = ZonedDateTime.of(1989, Month.JANUARY.getValue(), 10, 22, 58, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime zonedDate2 = ZonedDateTime.of(1989, Month.FEBRUARY.getValue(), 5, 1, 5, 45, 0, ZoneId.of("UTC"));
        doReturn(Arrays.asList(
                new TransactionDTO(1, null, 3, DEPOSIT, Date.from(zonedDate1.toInstant()), 45, "DEPOSIT"),
                new TransactionDTO(2, 2, null, WITHDRAWAL, Date.from(zonedDate2.toInstant()), 22.5, "WITHDRAWAL")
        )).when(transactionService).getAllTransactions();

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/transactions")
        .then()
            .statusCode(SC_OK)
            .body("size()", is(2),
                    "[0].id", is(1),
                    "[0].originAccount", is(nullValue()),
                    "[0].destinationAccount", is(3),
                    "[0].transactionType", is("DEPOSIT"),
                    "[0].date", is(dtf.format(zonedDate1)),
                    "[0].amount", is(45f),
                    "[0].comments", is("DEPOSIT"),
                    "[1].id", is(2),
                    "[1].originAccount", is(2),
                    "[1].destinationAccount", is(nullValue()),
                    "[1].transactionType", is("WITHDRAWAL"),
                    "[1].date", is(dtf.format(zonedDate2)),
                    "[1].amount", is(22.5f),
                    "[1].comments", is("WITHDRAWAL"));
    }

    @Test
    public void addTransaction() {
        Integer newId = 5;
        ZonedDateTime zonedDate = ZonedDateTime.now(ZoneId.of("UTC"));
        when(transactionRepository.save(any())).thenAnswer(invocation -> {
           Transaction transaction = invocation.getArgument(0);
           transaction.setId(newId);
           transaction.setDate(Date.from(zonedDate.toInstant()));
           return transaction;
        });
        stubDummyAccounts(50);

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("originAccount", 1)
                    .and("destinationAccount", 8)
                    .and("transactionType", "TRANSFER")
                    .and("amount", 34.5f)
                    .and("comments", "test transaction")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/transactions")
        .then()
            .statusCode(SC_CREATED)
            .header(LOCATION,  Matchers.endsWith(REST_API_PREFIX + "/transactions/" + newId))
            .body("id", is(newId),
                    "originAccount", is(1),
                    "destinationAccount", is(8),
                    "transactionType", is("TRANSFER"),
                    "amount", is(34.5f),
                    "date", is(dtf.format(zonedDate)),
                    "comments", is("test transaction"));

        verify(transactionService, times(1)).addTransaction(any());
        verify(transactionRepository, times(1)).save(any());
        verify(accountRepository, times(1)).findById(1);
        verify(accountRepository, times(1)).findById(8);
    }

    @Test
    public void transferToSameAccount() {
        stubDummyAccounts(50);

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("originAccount", 1)
                    .and("destinationAccount", 1)
                    .and("transactionType", "TRANSFER")
                    .and("amount", 34.5f)
                    .and("comments", "test transaction")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/transactions")
        .then()
            .statusCode(SC_UNPROCESSABLE_ENTITY)
            .body("errors.size()", is(1),
                    "errors[0].title", is("Validation Error"),
                    "errors[0].detail", is("Cannot transfer to same account"));
    }

    @Test
    public void invalidTransaction() {
        stubDummyAccounts(50);

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("originAccount", 1)
                    .and("destinationAccount", 8)
                    .and("transactionType", "DEPOSIT")
                    .and("amount", 34.5)
                    .and("comments", "test transaction")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/transactions")
        .then()
            .statusCode(SC_UNPROCESSABLE_ENTITY)
            .body("errors.size()", is(1),
                    "errors[0].title", is("Validation Error"),
                    "errors[0].detail", is("DEPOSIT transaction mustn't have an origin account"));
    }

    @Test
    public void invalidAccountReference() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("originAccount", 1)
                    .and("destinationAccount", 8)
                    .and("transactionType", "TRANSFER")
                    .and("amount", 34.5)
                    .and("comments", "test transaction")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/transactions")
        .then()
            .statusCode(SC_UNPROCESSABLE_ENTITY)
            .body("errors.size()", is(1),
                    "errors[0].title", is("Invalid Entity Reference"),
                    "errors[0].detail", is("Invalid reference to Account with ID " + 1));
    }

    @Test
    public void badRequest() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("originAccount", 1)
                    .and("destinationAccount", 8)
                    .and("transactionType", "TRANSFER")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/transactions")
        .then()
            .statusCode(SC_BAD_REQUEST)
            .body("errors.size()", is(2),
                    "errors.title", containsInAnyOrder(is("Field Error"), is("Field Error")),
                    "errors.detail", containsInAnyOrder(containsString("'comments'"), containsString("'amount'")));
    }

    private void stubDummyAccounts(double amount) {
        when(accountRepository.findById(any())).thenAnswer(invocation -> {
            Account account = new Account();
            account.setId(invocation.getArgument(0));
            account.setBalance(amount);
            account.setStatus(AccountStatus.OPEN);
            account.setType(AccountType.PRIVATE);
            return Optional.of(account);
        });
    }
}
