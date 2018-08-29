package com.example.demoApp.rest;

import com.example.demoApp.controller.UserController;
import com.example.demoApp.model.User;
import com.example.demoApp.repository.UserRepository;
import com.example.demoApp.service.EntityNotFoundException;
import com.example.demoApp.unit.ServiceTestsConfiguration;
import com.example.demoApp.util.MapBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static com.example.demoApp.DemoAppApplication.REST_API_PREFIX;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.apache.http.HttpStatus.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

@WebMvcTest(controllers = UserController.class, secure = false)
public class UserApiTest extends RestApiTestBase {

    @MockBean
    private UserRepository userRepository;

    @Test
    public void getUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1, "John", "E.", "some street", "Jerusalem")));

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/users/{id}", 1)
        .then()
            .statusCode(SC_OK)
            .body("id", equalTo(1),
                    "firstName", equalTo("John"),
                    "lastName", equalTo("E."),
                    "address", equalTo("some street"),
                    "city", equalTo("Jerusalem"));
    }

    @Test
    public void getNonExistingUser() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/users/{id}", 1)
        .then()
            .statusCode(SC_NOT_FOUND);
    }

    @Test
    public void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
            new User(1, "John", "E.", "some street", "Jerusalem"),
            new User(2, "Nikita", "B.", "some other street", "Jerusalem")
        ));

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/users")
        .then()
            .statusCode(SC_OK)
            .body("size()", is(2),
                    "[0].firstName", equalTo("John"),
                    "[1].firstName", equalTo("Nikita"));
    }

    @Test
    public void createUser() {
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == null) {
                user.setId(1);
            }
            return user;
        });

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.from("firstName", "John")
                    .and("lastName", "E.")
                    .and("address", "some street")
                    .and("city", "Jerusalem")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/users")
        .then()
            .statusCode(SC_CREATED)
            .header(LOCATION, not(empty()))
            .body("id", equalTo(1),
                    "firstName", equalTo("John"),
                    "lastName", equalTo("E."),
                    "address", equalTo("some street"),
                    "city", equalTo("Jerusalem"));
    }

    @Test
    public void updateUser() {
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == null) {
                user.setId(1);
            }
            return user;
        });

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.<String, Object>from("id", 1)
                    .and("firstName", "John")
                    .and("lastName", "E.")
                    .and("address", "some other street")
                    .and("city", "Jerusalem")
                    .build())
        .when()
            .put(REST_API_PREFIX + "/users/{id}", 1)
        .then()
            .statusCode(SC_OK)
            .header(LOCATION, is(nullValue()))
            .body("id", equalTo(1),
                    "firstName", equalTo("John"),
                    "lastName", equalTo("E."),
                    "address", equalTo("some other street"),
                    "city", equalTo("Jerusalem"));
    }

    @Test
    public void deleteUser() {
        when(userRepository.findById(1))
                .thenReturn(Optional.of(new User(1, "John", "E.", "some street", "Jerusalem")));

        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .delete(REST_API_PREFIX + "/users/{id}", 1)
        .then()
            .statusCode(SC_NO_CONTENT);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).delete(any());
    }

    @Test
    public void deleteNonExistingUser() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .delete(REST_API_PREFIX + "/users/{id}", 1)
        .then()
            .statusCode(SC_NOT_FOUND)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .body("errors.size()", is(1),
                    "errors[0].title", is("Entity Not Found"),
                    "errors[0].detail", is("Cannot find entity User with ID " + 1));

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(0)).delete(any());
    }

    @Test
    public void dataValidation() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .body(MapBuilder.from("firstName", "John")
                    .and("lastName", "")
                    .and("address", StringUtils.repeat("very long name ", 10))
                    .and("city", "Jerusalem")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/users")
        .then()
            .statusCode(SC_BAD_REQUEST)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .body("errors.size()", is(2),
                    "errors.title", contains(Arrays.asList(is("Field Error"), is("Field Error"))));
    }

    @Test
    public void UnknownEndpoint() {
        given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/usersTypo/{id}", 1)
        .then()
            .statusCode(SC_NOT_FOUND)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .body("errors.size()", is(1),
                    "errors[0].title", is("Unknown Endpoint"));
    }
}
