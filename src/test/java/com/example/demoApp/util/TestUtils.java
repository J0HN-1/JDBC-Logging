package com.example.demoApp.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;

public class TestUtils {

    public static void cleanDB(DataSource ds) {
        Connection connection = null;

        try {
            connection = ds.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            System.out.println();
            Statement st = connection.createStatement();

            st.execute("SET REFERENTIAL_INTEGRITY FALSE");

            ResultSet rs = databaseMetaData.getTables(null, null, null, null);
            while (rs.next()) {
                if (!rs.getString("TABLE_SCHEM").toUpperCase().equals("INFORMATION_SCHEMA")) {
                    st.execute("TRUNCATE TABLE " + rs.getString("TABLE_NAME"));
                }
            }

            st.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception ignore) {
            }
        }
    }
}
