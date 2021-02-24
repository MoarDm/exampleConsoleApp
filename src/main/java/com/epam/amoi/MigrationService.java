package com.epam.amoi;

import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MigrationService {
    private final Connection connection;

    public void initialize() {
        try {
            final String sqlInitializationScript = fetchInitializationScript();
            final PreparedStatement preparedStatement = connection.prepareStatement(sqlInitializationScript);
            preparedStatement.execute();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private String fetchInitializationScript() {
        final InputStream in = getClass().getResourceAsStream("/db/migration/initialize.sql");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader.lines()
                .collect(Collectors.joining("\n"));
    }

}
