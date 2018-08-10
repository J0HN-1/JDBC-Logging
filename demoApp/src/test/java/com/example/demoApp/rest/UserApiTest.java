package com.example.demoApp.rest;

import com.example.demoApp.controller.UserController;
import com.example.demoApp.model.User;
import com.example.demoApp.repository.UserRepository;
import com.example.demoApp.unit.ServiceTestsConfiguration;
import com.example.demoApp.util.MapBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
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

@WebMvcTest(controllers = UserController.class, secure = false)
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = "com.example.demoApp.controller")
@ContextConfiguration(classes = ServiceTestsConfiguration.class)
public class UserApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void initMockMvc() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void getUser() {
        when(userRepository.findById(any())).thenAnswer(invocation -> {
            int id = invocation.getArgument(0);
            return id == 1 ? Optional.of(new User(1, "John", "E.", "some street", "Jerusalem")) : Optional.empty();
        });

        given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/users/{id}", 1)
        .then()
            .statusCode(200)
            .body("id", equalTo(1),
                    "firstName", equalTo("John"),
                    "lastName", equalTo("E."),
                    "address", equalTo("some street"),
                    "city", equalTo("Jerusalem"));
    }

    @Test
    public void getNonExistingUser() {
        given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/users/{id}", 1)
        .then()
            .statusCode(404);
    }

    @Test
    public void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
            new User(1, "John", "E.", "some street", "Jerusalem"),
            new User(2, "Nikita", "B.", "some other street", "Jerusalem")
        ));

        given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .get(REST_API_PREFIX + "/users")
        .then()
            .statusCode(200)
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
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .body(MapBuilder.init()
                    .put("firstName", "John")
                    .put("lastName", "E.")
                    .put("address", "some street")
                    .put("city", "Jerusalem")
                    .build())
        .when()
            .post(REST_API_PREFIX + "/users")
        .then()
            .statusCode(201)
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
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .body(MapBuilder.init()
                    .put("id", 1)
                    .put("firstName", "John")
                    .put("lastName", "E.")
                    .put("address", "some other street")
                    .put("city", "Jerusalem")
                    .build())
        .when()
            .put(REST_API_PREFIX + "/users/{id}", 1)
        .then()
            .statusCode(200)
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
                .delete(REST_API_PREFIX + "/users/{id}", 1)
                .then()
                .statusCode(204);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).delete(any());
    }

    @Test
    public void deleteNonExistingUser() {
        given()
                .delete(REST_API_PREFIX + "/users/{id}", 1)
                .then()
                .statusCode(404);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(0)).delete(any());
    }
}
