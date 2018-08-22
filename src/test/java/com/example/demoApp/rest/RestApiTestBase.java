package com.example.demoApp.rest;

import com.example.demoApp.unit.ServiceTestsConfiguration;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.example.demoApp.controller", "com.example.demoApp.exception"})
@ContextConfiguration(classes = ServiceTestsConfiguration.class)
@Transactional
public abstract class RestApiTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void initMockMvc() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }
}
