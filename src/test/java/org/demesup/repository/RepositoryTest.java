package org.demesup.repository;

import junit.framework.TestCase;
import org.demesup.model.Department;
import org.demesup.model.Employee;
import org.demesup.model.field.Gender;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.demesup.model.field.DepartmentField.LOCATION;
import static org.demesup.model.field.EmployeeField.*;

public class RepositoryTest extends TestCase {
    Repository repository = new Repository();

    @Test
    public void testSaveEmployee() {
        Employee employee = getTestEmployee();
        repository.save(employee);

        assert (employee.equals(repository.getById(Employee.class, employee.getEmpId()).get()));
        repository.delete(employee.getDepartment());
    }

    @Test
    public void saveExistingEmployeeDoesNotWork() {
        Employee employee = getTestEmployee();
        repository.save(employee);
    }

    @Test
    public void testSaveDepartment() {
        Department department = getTestDepartment();
        repository.save(department);
        assert (department.equals(repository.getById(Department.class, department.getDep_id()).get()));
        repository.delete(department);
    }

    @Test
    public void testUpdate() {
        Department department = getTestDepartment();
        repository.save(department);

        department.setLocation("lalalalal");
        repository.update(department);
        repository.delete(department);
    }

    @Test
    public void testUpdateAll() {
        repository.updateAll(Department.class, Map.of(LOCATION, "HAWAII"));
    }

    @Test
    public void testUpdateAllByFields() {
        repository.updateAllByFields(Employee.class, Map.of(GENDER, List.of("female")), Map.of(SALARY, "99999"));
    }

    @Test
    public void testGetById() {
        Department department = getTestDepartment();
        repository.save(department);
        repository.getById(Department.class, department.getDep_id());
    }

    @Test
    public void testGetAll() {
        System.out.println(repository.getAll(Department.class));
        repository.getAll(Employee.class);
    }

    @Test
    public void testGetAllByFields() {
        System.out.println(repository.getAllByFields(Employee.class, Map.of(GENDER, List.of("female"))));
    }

    @Test
    public void testDelete() {
        Department department = getTestDepartment();
        repository.save(department);
        repository.delete(department);
    }

    @Test
    public void testDeleteAll() {
        List<Employee> employees = repository.getAll(Employee.class);
        repository.deleteAll(Employee.class);
        repository.saveAll(employees);
    }

    @Test
    public void testDeleteAllByFields() {
        repository.deleteAllByFields(Department.class, Map.of(NAME, List.of("testname", "newTestName")));
    }

    static Employee getTestEmployee() {
        return new Employee("testname", "testsurname", Gender.MALE, 1, "testemail@test.tst", getTestDepartment());
    }

    static Department getTestDepartment() {
        return new Department("testname", "testlocation");
    }

    static Department diffDepartment() {
        return new Department("test" + 5, "test" + 8);
    }
}