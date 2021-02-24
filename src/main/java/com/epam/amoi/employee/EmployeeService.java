package com.epam.amoi.employee;

import com.epam.amoi.employee.dao.EmployeeDao;
import com.epam.amoi.employee.dao.ProjectDao;
import com.epam.amoi.employee.dao.SkillsDao;
import com.epam.amoi.employee.model.Employee;
import com.epam.amoi.employee.model.EmployeeInput;
import com.epam.amoi.employee.model.Project;
import com.epam.amoi.employee.model.Technology;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
public class EmployeeService {
    private final EmployeeDao employeeDao;
    private final ProjectDao projectDao;
    private final SkillsDao skillsDao;

    public EmployeeService(final Connection connection) {
        employeeDao = new EmployeeDao(connection);
        projectDao = new ProjectDao(connection);
        skillsDao = new SkillsDao(connection);
    }


    public Integer create(final EmployeeInput employeeInput) {
        Objects.requireNonNull(employeeInput);

        try {
            final Project project = projectDao.insertNonExistedProject(employeeInput.getProject());
            skillsDao.insertNonExistedSkills(employeeInput);
            final Set<Technology> techSet = skillsDao.findTechnologiesByName(employeeInput.getTechnology());
            return employeeDao.create(employeeInput, project, techSet);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Employee> select(final int limit, final int offset) {
        return employeeDao.selectPage(limit, offset);
    }
}
