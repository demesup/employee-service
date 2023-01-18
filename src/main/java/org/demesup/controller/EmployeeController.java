package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.model.Employee;
import org.demesup.model.field.EmployeeField;
import org.demesup.model.field.Field;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;

import static org.utils.Read.readPositiveNumber;
import static org.utils.Utils.listInSeparatedLines;

@Slf4j
public class EmployeeController extends Controller {
    @Override
    public Employee create() {
        Employee employee = new Employee();
        Arrays.stream(EmployeeField.values()).forEach(f -> f.set(employee));
        repository.save(employee);
        return employee;
    }

    @Override
    public void delete() {
        delete(Employee.class);
    }

    @Override
    public void read() {
        log.debug(listInSeparatedLines(repository.getAll(Employee.class)));
    }

    @Override
    protected Map<Field, List<String>> getValues(Field[] fields, List<Integer> indexes) {
        Map<Field, List<String>> map = new HashMap<>();
        IntStream.range(0, fields.length)
                .filter(indexes::contains)
                .mapToObj(i -> fields[i])
                .forEachOrdered(f -> {
                    if (EmployeeField.DEPARTMENT.equals(f)) {
                        try {
                            map.put(EmployeeField.DEPARTMENT,
                                    List.of(new BufferedReader(new InputStreamReader(System.in)).readLine().split("\\s")));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (EmployeeField.GENDER.equals(f)) {
                        map.put(f, f.valueFromUser());
                    } else {
                        map.put(f, getValues(f));
                    }
                });
        return map;
    }

    @Override
    @SneakyThrows
    public void update() {
        update(Employee.class, EmployeeField.values());
    }

    @SneakyThrows
    protected Optional<Employee> getById() {
        return repository.getById(Employee.class, readPositiveNumber("Enter id"));
    }

    @Override
    @SneakyThrows
    protected Optional<Employee> getOneByFields() {
        return getOneByFields(Employee.class);
    }

}
