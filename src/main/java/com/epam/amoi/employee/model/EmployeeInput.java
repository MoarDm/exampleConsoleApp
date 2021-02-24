package com.epam.amoi.employee.model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Data
public class EmployeeInput {

    private final String firstName;
    private final String lastName;
    private final EmployeeType employeeType;
    @Nullable
    private final String project;
    private final Set<String> technology;
}
