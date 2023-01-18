package org.demesup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.controller.Controller;
import org.demesup.model.Model;
import org.hibernate.ObjectNotFoundException;
import org.utils.Read;
import org.utils.exception.ExitException;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

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
        public static final Map<Action, Function<Class<? extends Model>, String>> queryStartMap = Map.of(
                UPDATE, cl -> "update " + cl.getSimpleName() + " c where",
                SEARCH, cl -> "select * from " + cl.getSimpleName() + " where",
                DELETE, cl -> "delete from " + cl.getSimpleName() + " c where"
        );
    }


    public static void start() {
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
        }finally {
            HibernateUtil.finish();
        }
    }
}
