package com.epam.amoi.employee.model;

import lombok.Data;

import java.util.Set;

@Data
public class Employee {

    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final EmployeeType employeeType;
    private final Project project;
    private final Set<Technology> technology;
}
