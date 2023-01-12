package org.demesup.model.field;

import lombok.Getter;
import lombok.SneakyThrows;
import org.demesup.ModelType;
import org.demesup.controller.Controller;
import org.demesup.model.Department;
import org.demesup.model.Employee;
import org.demesup.model.Model;

import java.util.function.Function;

import static org.utils.Patterns.askStringWhileDoesNotMatchToPattern;
import static org.utils.Patterns.emailPattern;
import static org.utils.Read.*;

@Getter
public enum EmployeeField implements Field {
    NAME(Employee::getName) {
        @Override
        public void set(Employee employee) {
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
        public void set(Employee employee) {
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
        public void set(Employee employee) {
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
        public void set(Employee employee) {
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
        public void set(Employee employee) {
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
        public void set(Employee employee) {
            employee.setDepartment(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public Department valueFromUser() {
            try {
                Department department;
                Controller controller = ModelType.DEPARTMENT.getController();
                controller.read();
                if (inputEqualsYes("Choose an existing department?")) {
                    department = controller.search();
                } else department = controller.create();
                return department;
            } catch (Exception e) {
                if (inputEqualsYes("Try again?")) {
                    return valueFromUser();
                } else throw new RuntimeException(e);
            }
        }
    };
    final Function<Employee, Object> getter;

    EmployeeField(Function<Employee, Object> getter) {
        this.getter = getter;
    }

    public abstract void set(Employee employee);

    @Override
    public <T extends Model> void setter(T model) {
        if (!(model instanceof Employee)) throw new RuntimeException("Notify the developer");
        set((Employee) model);

    }
}
