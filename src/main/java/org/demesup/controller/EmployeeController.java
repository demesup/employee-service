package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.NoModelWithSuchParametersException;
import org.demesup.model.Employee;
import org.demesup.model.field.EmployeeField;
import org.hibernate.ObjectNotFoundException;

import java.util.Arrays;
import java.util.Optional;

import static org.utils.Read.*;
import static org.utils.Utils.listInSeparatedLines;
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
    @Override
    protected Employee getModel() {
        try {
            Optional<Employee> optional =
                    inputEqualsYes("Do you know id of employee?") ? getById() : getOneByFields();
            if (optional.isEmpty()) throw new NoModelWithSuchParametersException();
            return optional.get();
        } catch (ObjectNotFoundException e) {
            throw new NoModelWithSuchParametersException();
        }
    }

    @SneakyThrows
    protected Optional<Employee> getById() {
        return repository.getById(Employee.class, readPositiveNumber("Enter id"));
    }


    @Override
    public Employee search() {
        Employee employee = getModel();
        log.debug(employee.toString());
        return employee;
    }

    @Override
    @SneakyThrows
    protected Optional<Employee> getOneByFields() {
        var result = getListByFields(Employee.class);
        if (result.size() == 1) return Optional.of(result.get(0));
        log.debug(listInSeparatedLines(result));
        int index = readNumber(result.size(), "Enter index");
        return Optional.of(result.get(index));
    }
}
