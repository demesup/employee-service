package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.model.Department;
import org.demesup.model.DepartmentField;
import org.hibernate.ObjectNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.demesup.AppController.Action.SEARCH;
import static org.demesup.AppController.Action.UPDATE;
import static org.utils.Read.*;
import static org.utils.Utils.listWithTitle;

@Slf4j
public class DepartmentController extends Controller {
    @Override
    public void create() {
        Department department = new Department();
        Arrays.stream(DepartmentField.values()).forEach(f -> f.setter(department));
        repository.save(department);
    }

    @Override
    public void delete() {
        Department department = getDepartment();
        repository.delete(department);
    }

    @Override
    public void read() {
        log.debug(listWithTitle(repository.getAll(Department.class)));
    }

    @Override
    @SneakyThrows
    public void update() {
        Department department = search();
        repository.update(updated(department));
    }

    @SneakyThrows
    protected Department updated(Department department) {
        var indexes = getIndexes(UPDATE, DepartmentField.values());
        Arrays.stream(DepartmentField.values()).filter(f -> indexes.contains(f.ordinal())).forEach(f -> f.setter(department));
        return department;
    }

    @SneakyThrows
    private Department getDepartment() {
        try {
            Optional<Department> optional = inputEqualsYes("Do you know id of department to update?") ? getById() : getByFields();
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
        Department department = getDepartment();
        log.debug(department.toString());
        return department;
    }

    @Override
    @SneakyThrows
    protected Optional<Department> getByFields() {
        var result = getListByFields();
        if (result.size() == 1) return Optional.of(result.get(0));
        int index = readNumber(result.size(), "Enter index");
        return Optional.of(result.get(index));
    }

    @Override
    @SneakyThrows
    protected List<Department> getListByFields() {
        var indexes = getIndexes(SEARCH, DepartmentField.values());
        Map<String, String> map = Arrays.stream(DepartmentField.values())
                .filter(f -> indexes.contains(f.ordinal()))
                .collect(Collectors.toMap(
                        field -> field.toString().toLowerCase(),
                        field -> field.valueFromUser().toString()));
        List<Department> departments = repository.getByFields(Department.class, map);
        if (departments.isEmpty()) throw new NoModelWithSuchParametersException(map.toString());
        return departments;
    }
}
