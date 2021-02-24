package com.epam.amoi;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
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
        try {
            final String pathString = Objects.requireNonNull(this.getClass().getClassLoader()
                    .getResource("db/migration/initialize.sql"))
                    .getPath();
            final Path path = FileSystems.getDefault().getPath(pathString);
            return Files.lines(path)
                    .collect(Collectors.joining("\n"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
