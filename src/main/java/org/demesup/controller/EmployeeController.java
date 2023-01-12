package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.model.Employee;
import org.demesup.model.field.EmployeeField;

import java.util.Arrays;
import java.util.Optional;

import static org.utils.Read.readPositiveNumber;
import static org.utils.Utils.listWithTitle;

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
        log.debug(listWithTitle(repository.getAll(Employee.class)));
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
