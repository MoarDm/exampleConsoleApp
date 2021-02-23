package com.epam.amoi;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static final String DB_URL = "jdbc:h2:./exampleConsoleAppDb";
    public static final String DB_USER = "sa";
    public static final String DB_PASSWORD = "";

    public static void main(final String[] args) throws SQLException {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(DB_URL);
        ds.setUser(DB_USER);
        ds.setPassword(DB_PASSWORD);
        final JdbcConnectionPool jdbcConnectionPool = JdbcConnectionPool.create(ds);

        try (final Connection connection = jdbcConnectionPool.getConnection()) {
            final MigrationService migrationService = new MigrationService(connection);
            migrationService.initialize();
            final ExampleConsoleApp exampleConsoleApp = new ExampleConsoleApp(connection);
            exampleConsoleApp.start(args);
        }
    }
}
