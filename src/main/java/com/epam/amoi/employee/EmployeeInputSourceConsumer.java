package com.epam.amoi.employee;

import com.epam.amoi.employee.model.EmployeeInput;
import com.epam.amoi.employee.model.EmployeeType;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@AllArgsConstructor
public class EmployeeInputSourceConsumer {

    private final String inputFilePath;
    private final String columnDelimiter;
    private final String listDelimiter;


    public void consume(final Consumer<EmployeeInput> employeeConsumer) {

        final String pathString = Objects.requireNonNull(this.getClass().getClassLoader()
                .getResource(inputFilePath))
                .getPath();
        final Path path = FileSystems.getDefault().getPath(pathString);

        try (final Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
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
