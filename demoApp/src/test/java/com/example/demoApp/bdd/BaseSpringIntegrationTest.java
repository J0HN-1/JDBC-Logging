package com.example.demoApp.bdd;

import com.example.demoApp.unit.ServiceTestsConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ServiceTestsConfiguration.class)
public abstract class BaseSpringIntegrationTest {
}
