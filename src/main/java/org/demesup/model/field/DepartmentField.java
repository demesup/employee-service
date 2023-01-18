package org.demesup.model.field;

import lombok.Getter;
import lombok.SneakyThrows;
import org.demesup.model.Department;
import org.demesup.model.Model;

import java.util.function.Function;

import static org.utils.Read.read;
@Getter

public enum DepartmentField implements Field {
    NAME(Department::getName) {
        @Override
        public void set(Department department) {
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
        public void set(Department department) {
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

    public abstract void set(Department department);

    @Override
    public <T extends Model> void setter(T model) {
        if (!(model instanceof Department))throw new RuntimeException("Notify developer");
        set((Department) model);
    }
}
