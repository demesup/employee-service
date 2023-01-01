package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.model.Employee;
import org.demesup.model.EmployeeField;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.demesup.AppController.Action.SEARCH;
import static org.demesup.AppController.Action.UPDATE;
import static org.utils.Read.*;
import static org.utils.Utils.listWithTitle;
import static org.utils.Utils.numberedArray;

@Slf4j
public class EmployeeController extends Controller {
    @Override
    public void create() {
        Employee employee = new Employee();
        Arrays.stream(EmployeeField.values()).forEach(f -> f.setter(employee));
        repository.save(employee);
    }

    @Override
    public void delete() {
        Employee employee = search();
        repository.delete(employee);
    }

    @Override
    public void read() {
        log.debug(listWithTitle(repository.getAll(Employee.class)));
    }

    @Override
    @SneakyThrows
    public void update() {
        Employee employee = getEmployee();
        if (employee == null) return;
        repository.update(update(employee));
    }

    protected Employee update(Employee employee) throws IOException {
        log.debug(numberedArray(EmployeeField.values()));
        var indexes = getIndexes(UPDATE, EmployeeField.values());
        Arrays.stream(EmployeeField.values()).filter(f -> indexes.contains(f.ordinal())).forEach(f -> f.setter(employee));
        return employee;
    }

    private Employee getEmployee() throws IOException {
        Employee employee = inputEqualsYes("Do you know id of employee to update?") ? getById() : getByFields();

        if (employee != null) {
            log.debug(employee.toString());
        } else {
            log.debug("No employee with given parameters");
            return null;
        }
        return employee;
    }

    @SneakyThrows
    protected Employee getById() {
        return repository.getById(Employee.class, readPositiveNumber("Enter id")).orElse(this.getById());
    }

    @Override
    @SneakyThrows
    public Employee search() {
        Employee employee = inputEqualsYes("Do you know employee id?") ? getById() : getByFields();
        log.debug(employee.toString());
        return employee;
    }

    @Override
    @SneakyThrows
    protected Employee getByFields() {
        var result = getListByFields();
        if (result.size() == 1) return result.get(0);
        int index = readNumber(result.size(), "Enter index");
        return result.get(index);
    }

    @Override
    @SneakyThrows
    protected List<Employee> getListByFields() {
        var indexes = getIndexes(SEARCH, EmployeeField.values());
        Map<String, String> map = Arrays.stream(EmployeeField.values())
                .filter(f -> indexes.contains(f.ordinal()))
                .collect(Collectors.toMap(
                        field -> field.toString().toLowerCase(),
                        field -> field.valueFromUser().toString()));

        List<Employee> employees = repository.getByFields(Employee.class, map);
        if (employees.isEmpty()) throw new NoModelWithSuchParametersException(map.toString());
        return employees;
    }
}
