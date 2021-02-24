package com.epam.amoi.employee.model.dao;

import com.epam.amoi.employee.model.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDao {

    private final Connection connection;

    public EmployeeDao(final Connection connection) {

        this.connection = connection;
    }

    public Integer create(final EmployeeInput employeeInput) {
        try {
            final Project project = insertNonExistedProject(employeeInput);
            insertNonExistedSkills(employeeInput);
            final Set<Technology> techSet = findTechnologiesByName(employeeInput.getTechnology());
            return createEmployee(employeeInput, project, techSet);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addSkillsToEmployee(final Integer employeeId, final Set<Technology> techSet) throws SQLException {
        final String sqlScript = "INSERT INTO employee2skill(employee_id, skill_id) VALUES " + techSet.stream()
                .map(tech -> "(" + employeeId + "," + tech.getId() + ")")
                .collect(Collectors.joining(","));
        connection.prepareStatement(sqlScript).executeUpdate();
    }

    private Integer createEmployee(final EmployeeInput employeeInput, final Project project, final Set<Technology> techSet) throws SQLException {

        final PreparedStatement createEmployeeStatement = connection.prepareStatement(
                "INSERT INTO employee(first_name,\n" +
                        "last_name,\n" +
                        "employee_type,\n" +
                        "project_fk)\n" +
                        "VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
        createEmployeeStatement.setString(1, employeeInput.getFirstName());
        createEmployeeStatement.setString(2, employeeInput.getLastName());
        createEmployeeStatement.setInt(3, employeeInput.getEmployeeType().ordinal());
        createEmployeeStatement.setInt(4, project.getId());
        createEmployeeStatement.execute();

        try (final ResultSet generatedKeys = createEmployeeStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                final Integer employeeId = generatedKeys.getInt(1);
                addSkillsToEmployee(employeeId, techSet);
                return employeeId;
            }
        }

        throw new RuntimeException("Unexpected employee id not returned");
    }


    private Set<Technology> findTechnologiesByName(final Set<String> technologies) throws SQLException {
        final String formatedSql = String.format(
                "SELECT * FROM skill WHERE skill_name in (%1$s)",
                technologies.stream()
                        .map(s -> "'" + s + "'")
                        .collect(Collectors.joining(",")));
        final PreparedStatement findTechnologiesByNameStatement = connection.prepareStatement(formatedSql);
        try (final ResultSet resultSet = findTechnologiesByNameStatement.executeQuery()) {

            final Set<Technology> technologySet = new HashSet<>();
            while (resultSet.next()) {
                final int id = resultSet.getInt("id");
                final String techName = resultSet.getString("skill_name");
                technologySet.add(new Technology(id, techName));
            }
            return technologySet;
        }
    }

    private Project insertNonExistedProject(final EmployeeInput employeeInput) throws SQLException {
        final PreparedStatement insertProject = connection.prepareStatement(
                "INSERT INTO project(project_name)\n" +
                        "SELECT * FROM ( SELECT CAST(? as VARCHAR) as project_name) x\n" +
                        "WHERE NOT EXISTS (SELECT * FROM project WHERE project_name = CAST(? as VARCHAR))",
                Statement.RETURN_GENERATED_KEYS);
        insertProject.setString(1, employeeInput.getProject());
        insertProject.setString(2, employeeInput.getProject());
        insertProject.executeUpdate();

        try (final ResultSet generatedKeys = insertProject.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return new Project(generatedKeys.getInt(1), employeeInput.getProject());
            } else {
                return findProjectByName(employeeInput.getProject());
            }
        }
    }

    private Project findProjectByName(final String projectName) throws SQLException {
        final PreparedStatement findProjectByNameStatement = connection
                .prepareStatement("SELECT * FROM project WHERE project_name = ?");
        findProjectByNameStatement.setString(1, projectName);
        try (final ResultSet resultSet = findProjectByNameStatement.executeQuery()) {
            if (resultSet.next()) {
                return buildProject(resultSet);
            } else {
                return null;
            }
        }
    }

    private Project buildProject(final ResultSet resultSet) throws SQLException {
        final int projectId = resultSet.getInt("id");
        final String projectName = resultSet.getString("project_name");
        return new Project(projectId, projectName);
    }

    private void insertNonExistedSkills(final EmployeeInput employeeInput) throws SQLException {
        final String technologySelect =
                employeeInput.getTechnology().stream()
                        .map(s -> "SELECT '" + s + "'")
                        .collect(Collectors.joining(" union "));

        final String insertTechnologiesScript = String.format(
                "INSERT INTO skill(skill_name)\n" +
                        "SELECT * FROM ( " + technologySelect + " ) x\n" +
                        "WHERE NOT EXISTS (SELECT * FROM skill WHERE skill_name in (%1$s))",
                employeeInput.getTechnology().stream()
                        .map(s -> "'" + s + "'")
                        .collect(Collectors.joining(",")));
        final PreparedStatement insertTechnologies = connection.prepareStatement(insertTechnologiesScript);

        insertTechnologies.executeUpdate();
    }

    public List<EmployeeFull> selectPage(final int limit, final int offset) {
        try {
            final PreparedStatement selectEmployeesStatement = connection.prepareStatement("SELECT\n" +
                    "e.id,\n" +
                    "e.first_name,\n" +
                    "e.last_name,\n" +
                    "e.employee_type,\n" +
                    "p.id,\n" +
                    "p.project_name\n" +
                    "FROM employee e\n" +
                    "INNER JOIN project p on e.project_fk = p.id\n" +
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
            final Set<Integer> employeeIds = employees.stream().map(Employee::getId).collect(Collectors.toSet());
            final Map<Integer, Set<Technology>> skillSet = selectSkills(employeeIds);
            return employees.stream()
                    .map(employee -> new EmployeeFull(employee, skillSet.get(employee.getId())))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Integer, Set<Technology>> selectSkills(final Set<Integer> employeeIds) throws SQLException {

        final String sqlScript = String.format("SELECT e2s.employee_id, e2s.skill_id, s.skill_name\n" +
                        "FROM employee2skill e2s\n" +
                        "INNER JOIN skill s on e2s.skill_id = s.id\n" +
                        "WHERE e2s.employee_id in (%1$s)",
                employeeIds.stream()
                        .map(id -> "'" + id + "'")
                        .collect(Collectors.joining(",")));
        final PreparedStatement employee2skillsStatement = connection.prepareStatement(sqlScript);
        try (final ResultSet resultSet = employee2skillsStatement.executeQuery()) {
            final Map<Integer, Set<Technology>> result = new HashMap<>();
            while (resultSet.next()) {
                final int employeeId = resultSet.getInt("employee_id");
                final int skillId = resultSet.getInt("skill_id");
                final String skillName = resultSet.getString("skill_name");
                result.putIfAbsent(employeeId, new HashSet<>());
                result.get(employeeId).add(new Technology(skillId, skillName));
            }
            return result;
        }
    }

    private Employee buildEmployee(final ResultSet resultSet) throws SQLException {
        final int employeeId = resultSet.getInt("employee.id");
        final String firstName = resultSet.getString("first_name");
        final String lastName = resultSet.getString("last_name");
        final EmployeeType employeeType = EmployeeType.ofOrdinalCode(resultSet.getInt("employee_type"));
        final Integer projectId = resultSet.getInt("project.id");
        final String projectName = resultSet.getString("project_name");
        final Project project = new Project(projectId, projectName);
        return new Employee(employeeId, firstName, lastName, employeeType, project);
    }

    // getAll
    // getOne
}
