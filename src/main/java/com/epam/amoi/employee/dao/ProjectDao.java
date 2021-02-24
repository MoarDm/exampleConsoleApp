package com.epam.amoi.employee.dao;

import com.epam.amoi.employee.model.Project;
import com.sun.istack.internal.Nullable;
import lombok.AllArgsConstructor;

import java.sql.*;

@AllArgsConstructor
public class ProjectDao {

    private final Connection connection;

    @Nullable
    public Project insertNonExistedProject(@Nullable final String project) {
        if (project == null) return null;
        try {
            final PreparedStatement insertProject = connection.prepareStatement(
                    "INSERT INTO project(project_name)\n" +
                            "SELECT * FROM ( SELECT CAST(? as VARCHAR) as project_name) x\n" +
                            "WHERE NOT EXISTS (SELECT * FROM project WHERE project_name = CAST(? as VARCHAR))",
                    Statement.RETURN_GENERATED_KEYS);
            insertProject.setString(1, project);
            insertProject.setString(2, project);
            insertProject.executeUpdate();

            try (final ResultSet generatedKeys = insertProject.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Project(generatedKeys.getInt(1), project);
                } else {
                    return findProjectByName(project);
                }
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private Project findProjectByName(final String projectName) throws SQLException {
        final PreparedStatement findProjectByNameStatement = connection
                .prepareStatement("SELECT * FROM project WHERE project_name = ?");
        findProjectByNameStatement.setString(1, projectName);
        try (final ResultSet resultSet = findProjectByNameStatement.executeQuery()) {
            if (resultSet.next()) {
                return buildProject(resultSet);
            } else {
                // project not found
                return null;
            }
        }
    }

    private Project buildProject(final ResultSet resultSet) throws SQLException {
        final int projectId = resultSet.getInt("id");
        final String projectName = resultSet.getString("project_name");
        return new Project(projectId, projectName);
    }

}
