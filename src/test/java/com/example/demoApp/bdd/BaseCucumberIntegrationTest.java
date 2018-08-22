package com.example.demoApp.bdd;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/bdd/transactions_flow.feature")
public class BaseCucumberIntegrationTest {
}