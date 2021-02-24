package com.epam.amoi.employee;

import com.epam.amoi.employee.model.EmployeeInput;
import com.epam.amoi.employee.model.EmployeeType;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@AllArgsConstructor
public class EmployeeInputSourceConsumer {

    private final Path inputFilePath;
    private final String columnDelimiter;
    private final String listDelimiter;

    public EmployeeInputSourceConsumer(final String inputFilePath, final String columnDelimiter, final String listDelimiter) {
        this.columnDelimiter = columnDelimiter;
        this.listDelimiter = listDelimiter;

        final Path path = Paths.get(inputFilePath);

        if (!path.toFile().exists()) {
            throw new RuntimeException(path.toFile().getAbsolutePath() + " not exists");
        }
        if (!path.toFile().canRead()) {
            throw new RuntimeException(path.toFile().getAbsolutePath() + " cannot be read");
        }

        this.inputFilePath = path;
    }

    public void consume(final Consumer<EmployeeInput> employeeConsumer) {
        try (final Stream<String> stream = Files.lines(inputFilePath, StandardCharsets.UTF_8)) {
            stream.skip(1) // ignoring first line of csv file because it is headers
                    .map(this::parseEmployeeLine).forEach(employeeConsumer);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private EmployeeInput parseEmployeeLine(final String employeeInputString) {

        final String[] employeeAttributes = employeeInputString.split(columnDelimiter, 5);
//        order of parameters in csv line
//      * First Name;Last Name;Employee type(M for manager, D for developer);Project;Technology
        return new EmployeeInput(
                employeeAttributes[0],
                employeeAttributes[1],
                EmployeeType.ofCode(employeeAttributes[2]),
                employeeAttributes[3].isEmpty() ? null : employeeAttributes[3],
                splitTechnology(employeeAttributes[4], listDelimiter)
        );
    }

    // handling list of skills is not required but good point for extension
    private Set<String> splitTechnology(final String technologiesString, final String technologyDelimiter) {
        final Set<String> technologyNames = new HashSet<>();
        final String[] technologies = technologiesString.split(technologyDelimiter);
        if (technologies.length != 1 || !technologies[0].isEmpty()) {
            Collections.addAll(technologyNames, technologies);
        }
        return technologyNames;
    }

}
