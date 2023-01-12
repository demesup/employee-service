package org.demesup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.controller.Controller;
import org.demesup.model.Department;
import org.demesup.model.Employee;
import org.demesup.model.Model;
import org.demesup.model.field.Gender;
import org.demesup.repository.Repository;
import org.demesup.repository.RepositoryImpl;
import org.hibernate.ObjectNotFoundException;
import org.utils.Read;
import org.utils.exception.ExitException;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.utils.Read.readEnumValue;
import static org.utils.Utils.listInSeparatedLines;

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
        public static final Map<Action, Function<Class<? extends Model>, String>> queryStartMap = Map.of(
                UPDATE, cl -> "update " + cl.getSimpleName() + " where ",
                SEARCH, cl -> "select c from " + cl.getSimpleName() + " c where c.",
                DELETE, cl -> "delete from " + cl.getSimpleName() + " where "
        );
    }


    public static void start() {
        Repository repository = new RepositoryImpl();
        System.out.println(listInSeparatedLines(repository.getAll(Employee.class)));
        log.info("Controller started session");
        try {
            loop();
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        log.info("Controller ended session");
    }

    private static void loop() throws IOException {
        try {
            while (true) {
                Controller controller = readEnumValue(ModelType.values(), "Enter number of entity").controller;
                readEnumValue(Action.values(), "Enter number of action").action.accept(controller);
            }
        } catch (NoModelWithSuchParametersException | ObjectNotFoundException e) {
            log.debug(e.getMessage());
            loop();
        } catch (ExitException e) {
            if (Read.inputEqualsYes("Continue?")) loop();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
