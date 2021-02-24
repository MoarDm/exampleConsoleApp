package com.epam.amoi.employee;

import com.epam.amoi.employee.model.EmployeeInput;
import com.epam.amoi.employee.model.EmployeeType;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.testng.Assert.assertEquals;


public class EmployeeInputSourceConsumerTest {

    @Test
    public void testParsingEmployees_1() {

        final EmployeeInputSourceConsumer employeeInputSourceConsumer =
                new EmployeeInputSourceConsumer("src/test/resources/file1.csv", ";", ",");


        final EmployeeInterceptorConsumer employeeInterceptorConsumer = new EmployeeInterceptorConsumer();
        employeeInputSourceConsumer.consume(employeeInterceptorConsumer);


        final List<EmployeeInput> employees = employeeInterceptorConsumer.getConsumedEmployees();
        assertEquals(employees.size(), 2);
        try (final AutoCloseableSoftAssertions softAssert = new AutoCloseableSoftAssertions()) {
            softAssert.assertThat(employees.get(0).getFirstName())
                    .as("FirstName").isEqualTo("John");
            softAssert.assertThat(employees.get(0).getLastName())
                    .as("LastName").isEqualTo("Smith");
            softAssert.assertThat(employees.get(0).getEmployeeType())
                    .as("EmployeeType").isEqualTo(EmployeeType.MANAGER);
            softAssert.assertThat(employees.get(0).getProject())
                    .as("Project").isEqualTo("Everything Everywhere");
            softAssert.assertThat(employees.get(0).getTechnology())
                    .as("Technology").isEmpty();

            softAssert.assertThat(employees.get(1).getFirstName())
                    .as("FirstName").isEqualTo("Martin");
            softAssert.assertThat(employees.get(1).getLastName())
                    .as("LastName").isEqualTo("Fowler");
            softAssert.assertThat(employees.get(1).getEmployeeType())
                    .as("EmployeeType").isEqualTo(EmployeeType.DEVELOPER);
            softAssert.assertThat(employees.get(1).getProject())
                    .as("Project").isEqualTo(null);
            softAssert.assertThat(employees.get(1).getTechnology())
                    .as("Technology").containsOnly("Java");
        }
    }

    @Test
    public void testParsingEmployees_2() {

        final EmployeeInputSourceConsumer employeeInputSourceConsumer =
                new EmployeeInputSourceConsumer("src/test/resources/file2.csv", ";", ",");


        final EmployeeInterceptorConsumer employeeInterceptorConsumer = new EmployeeInterceptorConsumer();
        employeeInputSourceConsumer.consume(employeeInterceptorConsumer);


        final List<EmployeeInput> employees = employeeInterceptorConsumer.getConsumedEmployees();
        assertEquals(employees.size(), 3);
        try (final AutoCloseableSoftAssertions softAssert = new AutoCloseableSoftAssertions()) {
            softAssert.assertThat(employees.get(0).getFirstName())
                    .as("FirstName").isEqualTo("Joel");
            softAssert.assertThat(employees.get(0).getLastName())
                    .as("LastName").isEqualTo("Spolski");
            softAssert.assertThat(employees.get(0).getEmployeeType())
                    .as("EmployeeType").isEqualTo(EmployeeType.DEVELOPER);
            softAssert.assertThat(employees.get(0).getProject())
                    .as("Project").isNull();
            softAssert.assertThat(employees.get(0).getTechnology())
                    .as("Technology").containsOnly("Kotlin");

            softAssert.assertThat(employees.get(1).getFirstName())
                    .as("FirstName").isEqualTo("John");
            softAssert.assertThat(employees.get(1).getLastName())
                    .as("LastName").isEqualTo("Doe");
            softAssert.assertThat(employees.get(1).getEmployeeType())
                    .as("EmployeeType").isEqualTo(EmployeeType.MANAGER);
            softAssert.assertThat(employees.get(1).getProject())
                    .as("Project").isEqualTo("British Telecom");
            softAssert.assertThat(employees.get(1).getTechnology())
                    .as("Technology").isEmpty();

            softAssert.assertThat(employees.get(2).getFirstName())
                    .as("FirstName").isEqualTo("William");
            softAssert.assertThat(employees.get(2).getLastName())
                    .as("LastName").isEqualTo("Gates");
            softAssert.assertThat(employees.get(2).getEmployeeType())
                    .as("EmployeeType").isEqualTo(EmployeeType.DEVELOPER);
            softAssert.assertThat(employees.get(2).getProject())
                    .as("Project").isEqualTo(null);
            softAssert.assertThat(employees.get(2).getTechnology())
                    .as("Technology").containsOnly(".NET");
        }
    }

    private class EmployeeInterceptorConsumer implements Consumer<EmployeeInput> {

        private final List<EmployeeInput> consumedEmployees = new ArrayList<>();

        public List<EmployeeInput> getConsumedEmployees() {
            return consumedEmployees;
        }

        @Override
        public void accept(final EmployeeInput employeeInput) {
            consumedEmployees.add(employeeInput);
        }
    }
}