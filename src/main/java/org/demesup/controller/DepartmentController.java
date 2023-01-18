package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.model.Department;
import org.demesup.model.field.DepartmentField;

import java.util.Arrays;
import java.util.Optional;

import static org.utils.Read.readPositiveNumber;
import static org.utils.Utils.listInSeparatedLines;

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
        log.debug(listInSeparatedLines
                (repository.getAll(Department.class).stream().map(Department::moreInfo).toList()),
                "Department");
    }

    @Override
    @SneakyThrows
    public void update() {
        update(Department.class, DepartmentField.values());
    }

    @SneakyThrows
    protected Optional<Department> getById() {
        return repository.getById(Department.class, readPositiveNumber("Enter id"));
    }

    @Override
    @SneakyThrows
    protected Optional<Department> getOneByFields() {
        return getOneByFields(Department.class);
    }
}
