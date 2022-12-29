package org.demesup.model;

import lombok.SneakyThrows;
import org.utils.Read;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import static org.demesup.controller.AppController.controller;
import static org.utils.Patterns.askStringWhileDoesNotMatchToPattern;
import static org.utils.Patterns.emailPattern;
import static org.utils.Read.*;

public enum EmployeeField implements Field{
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
            controller.getAll(Department.class).forEach(System.out::println);
            int id = Read.readNumber("Enter id");
           return controller.getById(Department.class, id).orElseGet(() -> {
                try {
                    if (inputEqualsYes("Try again?")) {
                        return valueFromUser();
                    }
                    Department department = new Department();
                    Arrays.stream(DepartmentField.values()).forEach(f-> f.setter(department));
                    return department;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    };
    final Function<Employee, Object> getter;

    EmployeeField(Function<Employee, Object> getter) {
        this.getter = getter;
    }

    public abstract void setter(Employee employee);

    public abstract <T extends Object> T valueFromUser();
}
