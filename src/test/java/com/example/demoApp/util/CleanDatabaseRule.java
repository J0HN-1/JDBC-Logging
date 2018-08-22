package com.example.demoApp.util;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;

public class CleanDatabaseRule implements TestRule {

    private DataSource dataSource;

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } finally {
                    if (description.getAnnotation(Sql.class) != null) {
                        TestUtils.cleanDB(dataSource);
                    }
                }
            }
        };
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
