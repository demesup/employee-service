package org.demesup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.controller.Controller;
import org.demesup.controller.DepartmentController;
import org.demesup.controller.EmployeeController;
import org.demesup.model.Department;
import org.demesup.model.Employee;
import org.demesup.model.Model;
import org.utils.Read;
import org.utils.exception.ExitException;

import java.io.IOException;
import java.util.function.Consumer;

import static org.demesup.HibernateUtil.finish;
import static org.utils.Read.readEnumValue;

@Slf4j
public class AppController {
    @Getter
    @AllArgsConstructor
    public
    enum Action {
        CREATE(controller -> controller.create()),
        UPDATE(controller -> controller.update()),
        SEARCH(controller -> controller.search()),
        DELETE(controller -> controller.delete()),
        READ_ALL(controller -> controller.read()),
        EXIT(controller -> {
            throw new ExitException();
        });
        final Consumer<Controller> action;
    }

    @Getter
    public
    enum ControllerEnum {
        EMPLOYEE(Employee.class, new EmployeeController()),
        DEPARTMENT(Department.class, new DepartmentController());
        final Class<? extends Model> cl;
        final Controller controller;

        ControllerEnum(Class<? extends Model> cl, Controller controller) {
            this.cl = cl;
            this.controller = controller;
        }
    }

    public static void start() throws IOException {
        try {
            while (true) {
                loop();
            }
        } catch (ExitException e) {
            if (Read.inputEqualsYes("Continue?")) loop();
        } catch (Exception e) {
            log.warn(e.getMessage());
        } finally {
            finish();
        }
    }

    private static void loop() throws IOException {
        Controller controller = readEnumValue(ControllerEnum.values(), "Enter number of entity").controller;
        readEnumValue(Action.values(), "Enter number of action").action.accept(controller);
    }
}
