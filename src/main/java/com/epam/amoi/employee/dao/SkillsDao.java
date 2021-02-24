package com.epam.amoi.employee.dao;

import com.epam.amoi.employee.model.EmployeeInput;
import com.epam.amoi.employee.model.Technology;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SkillsDao {

    private final Connection connection;

    public void insertNonExistedSkills(final EmployeeInput employeeInput) throws SQLException {
        Objects.requireNonNull(employeeInput);
        if (employeeInput.getTechnology().size() == 0) return;

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

    public Set<Technology> findTechnologiesByName(final Set<String> technologies) throws SQLException {
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

}
