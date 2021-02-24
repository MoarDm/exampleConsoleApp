package com.epam.amoi.employee.model;

import com.sun.istack.internal.Nullable;
import lombok.Data;

@Data
public class Employee {

    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final EmployeeType employeeType;
    @Nullable
    private final Project project;
}
