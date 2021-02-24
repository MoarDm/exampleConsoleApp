package com.epam.amoi.employee.dao;

import com.epam.amoi.employee.model.*;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDao {

    private final Connection connection;

    public EmployeeDao(final Connection connection) {

        this.connection = connection;
    }

    public Integer create(final EmployeeInput employeeInput, @Nullable final Project project, final Set<Technology> techSet) {
        Objects.requireNonNull(employeeInput);
        Objects.requireNonNull(techSet);
        try {
            final PreparedStatement createEmployeeStatement = connection.prepareStatement(
                    "INSERT INTO employee(first_name,\n" +
                            "last_name,\n" +
                            "employee_type,\n" +
                            "project_fk)\n" +
                            "VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            createEmployeeStatement.setString(1, employeeInput.getFirstName());
            createEmployeeStatement.setString(2, employeeInput.getLastName());
            createEmployeeStatement.setInt(3, employeeInput.getEmployeeType().ordinal());
            if (project != null) {
                createEmployeeStatement.setInt(4, project.getId());
            } else {
                createEmployeeStatement.setNull(4, Types.INTEGER);
            }
            createEmployeeStatement.execute();

            try (final ResultSet generatedKeys = createEmployeeStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    final Integer employeeId = generatedKeys.getInt(1);
                    addSkillsToEmployee(employeeId, techSet);
                    return employeeId;
                }
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Expected employee was created but something went wrong");
    }

    public List<Employee> selectPage(final int limit, final int offset) {
        try {
            final PreparedStatement selectEmployeesStatement = connection.prepareStatement(
                    "SELECT\n" +
                            "e.id,\n" +
                            "e.first_name,\n" +
                            "e.last_name,\n" +
                            "e.employee_type,\n" +
                            "p.id,\n" +
                            "p.project_name,\n" +
                            "ARRAY_AGG(s.id || ':' || s.SKILL_NAME) AS skills\n" +
                            "FROM employee e\n" +
                            "LEFT JOIN project p on p.id = e.project_fk\n" +
                            "LEFT JOIN employee2skill e2s on e2s.employee_id = e.id\n" +
                            "LEFT JOIN skill s on e2s.skill_id = s.id\n" +
                            "GROUP BY e.id\n" +
                            "LIMIT ?\n" +
                            "OFFSET ?;");
            selectEmployeesStatement.setInt(1, limit);
            selectEmployeesStatement.setInt(2, offset);
            final List<Employee> employees = new ArrayList<>();
            try (final ResultSet resultSet = selectEmployeesStatement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(buildEmployee(resultSet));
                }
            }
            return employees;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addSkillsToEmployee(final Integer employeeId, final Set<Technology> techSet) throws SQLException {
        if (techSet.size() == 0) return;

        final String employee2skillValues = techSet.stream()
                .map(tech -> "(" + employeeId + "," + tech.getId() + ")")
                .collect(Collectors.joining(","));
        connection.prepareStatement(
                "INSERT INTO employee2skill(employee_id, skill_id)\n" +
                        "VALUES " + employee2skillValues)
                .executeUpdate();
    }

    private Employee buildEmployee(final ResultSet resultSet) throws SQLException {

        final int employeeId = resultSet.getInt("employee.id");
        final String firstName = resultSet.getString("first_name");
        final String lastName = resultSet.getString("last_name");
        final EmployeeType employeeType = EmployeeType.ofOrdinalCode(resultSet.getInt("employee_type"));

        final String projectName = resultSet.getString("project_name");
        final Project project;
        if (projectName != null) {
            final Integer projectId = resultSet.getInt("project.id");
            project = new Project(projectId, projectName);
        } else {
            project = null;
        }

        final Set<Technology> skills = new HashSet<>();
        final Array skillsArray = resultSet.getArray("skills");
        if (skillsArray != null) {
            try (final ResultSet skillsArrayResultSet = skillsArray.getResultSet()) {
                while (skillsArrayResultSet.next()) {
                    final String[] skillNotParsed = skillsArrayResultSet.getString(2).split(":");
                    skills.add(new Technology(Integer.parseInt(skillNotParsed[0]), skillNotParsed[1]));
                }
            }
        }

        return new Employee(employeeId, firstName, lastName, employeeType, project, skills);
    }

}
