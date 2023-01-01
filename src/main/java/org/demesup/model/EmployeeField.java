package org.demesup.model;

import lombok.Getter;
import lombok.SneakyThrows;
import org.demesup.controller.DepartmentController;

import java.util.function.Function;

import static org.utils.Patterns.askStringWhileDoesNotMatchToPattern;
import static org.utils.Patterns.emailPattern;
import static org.utils.Read.*;

@Getter
public enum EmployeeField implements Field {
    NAME(Employee::getName) {
        @Override
        public void setter(Employee employee) {
            employee.setName(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public String valueFromUser() {
            return read("Enter name");
        }
    },
    SURNAME(Employee::getSurname) {
        @Override
        public void setter(Employee employee) {
            employee.setSurname(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public String valueFromUser() {
            return read("Enter surname");
        }
    },
    GENDER(Employee::getGender) {
        @Override
        public void setter(Employee employee) {
            employee.setGender(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public Gender valueFromUser() {
            return readEnumValue(Gender.values(), "Enter gender number");
        }
    },
    SALARY(Employee::getSalary) {
        @Override
        public void setter(Employee employee) {
            employee.setSalary(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public Integer valueFromUser() {
            return readPositiveNumber("Enter salary");
        }
    },
    EMAIL(Employee::getEmail) {
        @Override
        public void setter(Employee employee) {
            employee.setEmail(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public String valueFromUser() {
            return askStringWhileDoesNotMatchToPattern(emailPattern(), "Enter email");
        }
    },
    DEPARTMENT(Employee::getDepartment) {
        @Override
        public void setter(Employee employee) {

            employee.setDepartment(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public Department valueFromUser() {
            Department department;
            do {
                department = new DepartmentController().search();
            } while (department == null);
            return department;
        }
    };
    final Function<Employee, Object> getter;

    EmployeeField(Function<Employee, Object> getter) {
        this.getter = getter;
    }

    public abstract void setter(Employee employee);

}
