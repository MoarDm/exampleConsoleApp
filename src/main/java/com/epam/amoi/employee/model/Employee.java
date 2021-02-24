package com.epam.amoi.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Employee {

    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final EmployeeType employeeType;
    @Nullable
    private final Project project;
    private final Set<Technology> skills;

    @Override
    public String toString() {
        final StringBuilder humanReadableInfo = new StringBuilder()
                .append(firstName).append(" ")
                .append(lastName).append(", ")
                .append(employeeType.toString().toLowerCase());
        if (project != null) {
            humanReadableInfo.append(", ").append(project.getProjectName());
        }
        if (skills.size() > 0) {
            humanReadableInfo.append(", ").append(skills.stream().map(Technology::getTechnologyName).collect(Collectors.joining(", ")));
        }
        return humanReadableInfo.toString();
    }
}
