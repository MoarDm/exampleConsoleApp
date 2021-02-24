package com.epam.amoi.employee.model;

import com.sun.istack.internal.Nullable;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class EmployeeFull {

    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final EmployeeType employeeType;
    @Nullable
    private final Project project;
    private final Set<Technology> skills;

    public EmployeeFull(final Employee employee, final Set<Technology> skills) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.employeeType = employee.getEmployeeType();
        this.project = employee.getProject();
        this.skills = skills;
    }

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
