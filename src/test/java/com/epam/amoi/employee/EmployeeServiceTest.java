package com.epam.amoi.employee;

import com.epam.amoi.MigrationService;
import com.epam.amoi.employee.model.Employee;
import com.epam.amoi.employee.model.EmployeeInput;
import com.epam.amoi.employee.model.EmployeeType;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EmployeeServiceTest {

    private Connection connection;

    @Before
    public void init() throws SQLException {
        final String DB_URL = "jdbc:h2:./exampleConsoleAppDb_test";
        final String DB_USER = "sa";
        final String DB_PASSWORD = "";
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(DB_URL);
        ds.setUser(DB_USER);
        ds.setPassword(DB_PASSWORD);
        final JdbcConnectionPool jdbcConnectionPool = JdbcConnectionPool.create(ds);
        connection = jdbcConnectionPool.getConnection();
        connection.setAutoCommit(false);
        final MigrationService migrationService = new MigrationService(connection);
        migrationService.initialize();
    }

    @After
    public void after() throws SQLException {
        connection.rollback();
    }

    @Test
    public void testCreateWithProjectAnd2skills() {
        final EmployeeService employeeService = new EmployeeService(connection);
        final Set<String> skills = new HashSet<>();
        skills.add(".Net");
        skills.add("Java");
        final EmployeeInput employeeInput = new EmployeeInput("John",
                "Doe",
                EmployeeType.MANAGER,
                "British Telecom",
                skills);
        employeeService.create(employeeInput);

        final List<Employee> employees = employeeService.select(1000, 0);
        final Employee employee = employees.get(0);

        assertThat(employee.toString()).matches(s ->
                s.equals("John Doe, manager, British Telecom, .Net, Java")
                        || s.equals("John Doe, manager, British Telecom, Java, .Net"));
    }

    @Test
    public void testCreateWithSkill() {
        final EmployeeService employeeService = new EmployeeService(connection);
        final Set<String> skills = new HashSet<>();
        skills.add("Java");
        final EmployeeInput employeeInput = new EmployeeInput("John",
                "Doe",
                EmployeeType.MANAGER,
                null,
                skills);
        employeeService.create(employeeInput);

        final List<Employee> employees = employeeService.select(1000, 0);
        final Employee employee = employees.get(0);

        assertThat(employee.toString()).isEqualTo("John Doe, manager, Java");
    }

    @Test
    public void testCreateWithProject() {
        final EmployeeService employeeService = new EmployeeService(connection);
        final Set<String> skills = new HashSet<>();
        final EmployeeInput employeeInput = new EmployeeInput(
                "John",
                "Doe",
                EmployeeType.MANAGER,
                "British Telecom",
                skills);
        employeeService.create(employeeInput);

        final List<Employee> employees = employeeService.select(1000, 0);
        final Employee employee = employees.get(0);

        assertThat(employee.toString()).isEqualTo("John Doe, manager, British Telecom");
    }
}
