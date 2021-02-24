package com.epam.amoi;

import com.epam.amoi.employee.EmployeeInputSourceConsumer;
import com.epam.amoi.employee.EmployeeService;

import java.nio.file.InvalidPathException;
import java.sql.Connection;

public class ExampleConsoleApp {
    private final EmployeeService employeeService;

    public ExampleConsoleApp(final Connection connection) {
        this.employeeService = new EmployeeService(connection);
    }

    public void start(final String[] args) {
        // Area of improvement: Apache CLI or something better for parsing args
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
            // Area of improvement: extract delimiters into properties
            System.out.println("Saving employees");
            employeeInputSourceConsumer.consume(employeeService::create);
            // Area of improvement: show progress of importing
            // Area of improvement: batch saving
        } catch (final InvalidPathException e) {
            printError("File path '" + inputFilePath + "' not found");
        } catch (final RuntimeException e) {
            printError(e.getLocalizedMessage());
        }
    }

    private void printError(final String errorMessage) {
        System.out.println("ERROR: " + errorMessage);
    }

    private void printAllPersonnel() {
        System.out.println("Employees list:");
        employeeService.select(500, 0).forEach(System.out::println);
    }
}
