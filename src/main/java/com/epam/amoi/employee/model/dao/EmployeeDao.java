package com.epam.amoi.employee.model.dao;

import com.epam.amoi.employee.model.EmployeeInput;

import java.sql.Connection;

public class EmployeeDao {

    private final Connection connection;

    public EmployeeDao(final Connection connection) {

        this.connection = connection;
    }

    public void create(final EmployeeInput employeeInput) {
        /**
         * inserting non-existing projects
         */
        /**
         * inserting non-existing technologies
         */
        /**
         * insert into employee(
         *                 first_name,
         *                 last_name,
         *                 employee_type,
         *                 project_fk)
         * values(:firstName,
         *        :lastName,
         *        :employeeType,
         *        :project)
         * returning id
         */
        /**
         * inserting employee2skill relation
         */
        // maybe return id
    }

    // getAll
    // getOne
}
