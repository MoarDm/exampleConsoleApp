package com.epam.amoi;

import com.epam.amoi.employee.EmployeeInputSourceConsumer;
import com.epam.amoi.employee.model.dao.EmployeeDao;

import java.sql.Connection;

public class ExampleConsoleApp {
    private final EmployeeDao employeeDao;

    public ExampleConsoleApp(final Connection connection) {
        this.employeeDao = new EmployeeDao(connection);
    }

    public void start(final String[] args) {
        // maybe parse args into command stack
        switch (args.length) {
            case 0:
                printAllPersonnel();
                break;
            case 1:
                storeNewPersonnel(args[0]);
                break;
            default:
                printError("Required one valid parameter of csv file");
        }
        //        new Stack<>().pop();

    }

    private void storeNewPersonnel(final String inputFilePath) {
        final EmployeeInputSourceConsumer employeeInputSourceConsumer =
                new EmployeeInputSourceConsumer(inputFilePath, ";", ",");
        // maybe extract into properties
        employeeInputSourceConsumer.consume(employeeDao::create);
    }

    private void printError(final String errorMessage) {

    }

    private void printAllPersonnel() {
        // dao.selectAll()
    }
}
