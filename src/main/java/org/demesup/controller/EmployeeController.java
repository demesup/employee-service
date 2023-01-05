package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.ModelType;
import org.demesup.NoModelWithSuchParametersException;
import org.demesup.model.Employee;
import org.demesup.model.field.EmployeeField;
import org.hibernate.ObjectNotFoundException;

import java.util.Arrays;
import java.util.Optional;

import static org.demesup.AppController.Action.UPDATE;
import static org.utils.Read.*;
import static org.utils.Utils.listInSeparatedLines;
import static org.utils.Utils.listWithTitle;

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
        log.debug(listWithTitle(repository.getAll(ModelType.EMPLOYEE)));
    }

    @Override
    @SneakyThrows
    public void update() {
        Employee employee = search();
        repository.update(update(employee));
    }

    @SneakyThrows
    protected Employee update(Employee employee) {
        var indexes = getIndexes(UPDATE, EmployeeField.values());
        Arrays.stream(EmployeeField.values()).filter(f -> indexes.contains(f.ordinal())).forEach(f -> f.setter(employee));
        return employee;
    }

    @SneakyThrows
    private Employee getEmployee() {
        try {
            Optional<Employee> optional =
                    inputEqualsYes("Do you know id of employee to update?") ? getById() : getByFields();
            if (optional.isEmpty()) throw new NoModelWithSuchParametersException();
            return optional.get();
        } catch (ObjectNotFoundException e) {
            throw new NoModelWithSuchParametersException();
        }
    }

    @SneakyThrows
    protected Optional<Employee> getById() {
        return repository.getById(ModelType.EMPLOYEE, Employee.class, readPositiveNumber("Enter id"));
    }


    @Override
    public Employee search() {
        Employee employee = getEmployee();
        log.debug(employee.toString());
        return employee;
    }

    @Override
    @SneakyThrows
    protected Optional<Employee> getByFields() {
        var result = getListByFields(ModelType.EMPLOYEE, Employee.class);
        log.debug(listInSeparatedLines(result));
        if (result.size() == 1) return Optional.of((Employee) result.get(0));
        int index = readNumber(result.size(), "Enter index");
        return Optional.of(result.get(index));
    }
}
