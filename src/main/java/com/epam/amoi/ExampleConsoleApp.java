package com.epam.amoi;

import com.epam.amoi.employee.EmployeeInputSourceConsumer;
import com.epam.amoi.employee.EmployeeService;

import java.sql.Connection;

public class ExampleConsoleApp {
    private final EmployeeService employeeService;

    public ExampleConsoleApp(final Connection connection) {
        this.employeeService = new EmployeeService(connection);
    }

    public void start(final String[] args) {
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
    }

    private void storeNewPersonnel(final String inputFilePath) {
        try {
            final EmployeeInputSourceConsumer employeeInputSourceConsumer =
                    new EmployeeInputSourceConsumer(inputFilePath, ";", ",");
            employeeInputSourceConsumer.consume(employeeService::create);
        } catch (final RuntimeException e) {
            printError(e.getLocalizedMessage());
        }
        // maybe extract into properties
    }

    private void printError(final String errorMessage) {
        System.out.println("ERROR: " + errorMessage);
    }

    private void printAllPersonnel() {
        // dao.selectAll()
    }
}
