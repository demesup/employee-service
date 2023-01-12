package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.NoModelWithSuchParametersException;
import org.demesup.model.Department;
import org.demesup.model.field.DepartmentField;
import org.hibernate.ObjectNotFoundException;

import java.util.Arrays;
import java.util.Optional;

import static org.utils.Read.*;
import static org.utils.Utils.listInSeparatedLines;
import static org.utils.Utils.listWithTitle;

@Slf4j
public class DepartmentController extends Controller {
    @Override
    public Department create() {
        Department department = new Department();
        Arrays.stream(DepartmentField.values()).forEach(f -> f.set(department));
        repository.save(department);
        return department;
    }

    @SneakyThrows
    @Override
    public void delete() {
        delete(Department.class);
    }

    @Override
    public void read() {
        log.debug(listWithTitle(repository.getAll(Department.class)));
    }

    @Override
    @SneakyThrows
    public void update() {
        update(Department.class, DepartmentField.values());
    }

    @Override
    @SneakyThrows
    protected Department getModel() {
        try {
            Optional<Department> optional = inputEqualsYes("Do you know id of department?") ? getById() : getOneByFields();
            if (optional.isEmpty()) throw new NoModelWithSuchParametersException();
            return optional.get();
        } catch (ObjectNotFoundException e) {
            throw new NoModelWithSuchParametersException();
        }
    }

    @SneakyThrows
    protected Optional<Department> getById() {
        return repository.getById(Department.class, readPositiveNumber("Enter id"));
    }

    @Override
    public Department search() {
        Department department = getModel();
        log.debug(department.toString());
        return department;
    }

    @Override
    @SneakyThrows
    protected Optional<Department> getOneByFields() {
        var result = getListByFields(Department.class);
        if (result.size() == 1) return Optional.of(result.get(0));
        log.debug(listInSeparatedLines(result));
        int index = readNumber(result.size(), "Enter index");
        return Optional.of(result.get(index));
    }
}
