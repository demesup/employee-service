package org.demesup.model;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.function.Function;

import static org.utils.Read.read;
@Getter

public enum DepartmentField implements Field {
    NAME(Department::getName) {
        @Override
        public void setter(Department department) {
            department.setName(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public String valueFromUser() {
            return read("Enter name");
        }
    },
    LOCATION(Department::getLocation) {
        @Override
        public void setter(Department department) {
            department.setLocation(this.valueFromUser());
        }

        @SneakyThrows
        @Override
        public String valueFromUser() {
            return read("Enter location");
        }
    };
    final Function<Department, Object> getter;

    DepartmentField(Function<Department, Object> getter) {
        this.getter = getter;
    }

    public abstract void setter(Department department);

}
