package org.demesup.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.demesup.model.Department;
import org.demesup.model.DepartmentField;

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
public class DepartmentController extends Controller {
    @Override
    public void create() {
        Department department = new Department();
        Arrays.stream(DepartmentField.values()).forEach(f -> f.setter(department));
        repository.save(department);
    }

    @Override
    public void delete() {
        Department department = search();
        repository.delete(department);
    }

    @Override
    public void read() {
        log.debug(listWithTitle(repository.getAll(Department.class)));
    }

    @Override
    @SneakyThrows
    public void update() {
        Department department = getDepartment();
        if (department == null) return;
        repository.update(update(department));
    }


    protected Department update(Department department) throws IOException {
        log.debug(numberedArray(DepartmentField.values()));
        var indexes = getIndexes(UPDATE, DepartmentField.values());
        Arrays.stream(DepartmentField.values()).filter(f -> indexes.contains(f.ordinal())).forEach(f -> f.setter(department));
        return department;
    }

    private Department getDepartment() throws IOException {
        Department department = inputEqualsYes("Do you know id of department to update?") ? getById() : getByFields();

        if (department != null) {
            log.debug(department.toString());
        } else {
            log.debug("No department with given parameters");
            return null;
        }
        return department;
    }

    @SneakyThrows
    protected Department getById() {
        int id = readPositiveNumber("Enter id");
        return repository.getById(Department.class, id).orElseThrow(NoModelWithSuchParametersException::new);
    }

    @Override
    @SneakyThrows
    public Department search() {
        Department department = inputEqualsYes("Do you know department id?") ? getById() : getByFields();
        log.debug(department.toString());
        return department;
    }

    @Override
    @SneakyThrows
    protected Department getByFields() {
        var result = getListByFields();
        if (result.size() == 1) return result.get(0);
        int index = readNumber(result.size(), "Enter index");
        return result.get(index);
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
