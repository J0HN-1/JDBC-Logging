package com.example.demoApp.rest;

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

import static com.example.demoApp.DemoAppApplication.REST_API_PREFIX;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@WebMvcTest(secure = false)
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
    public void createUser() {
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1);
            return user;
        });

        RestAssuredMockMvc
                .given()
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

        reset(userRepository);
    }
}
