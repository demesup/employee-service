package org.demesup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demesup.controller.Controller;
import org.hibernate.ObjectNotFoundException;
import org.utils.Read;
import org.utils.exception.ExitException;

import java.io.IOException;
import java.util.function.Consumer;

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


    public static void start() {
        try {
            loop();
        } catch (Throwable e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loop() throws IOException {
        try {
            while (true) {
                Controller controller = readEnumValue(ModelType.values(), "Enter number of entity").controller;
                readEnumValue(Action.values(), "Enter number of action").action.accept(controller);
            }
        } catch (NoModelWithSuchParametersException | ObjectNotFoundException e) {
            log.debug(e.getMessage());
            start();
        } catch (ExitException e) {
            if (Read.inputEqualsYes("Continue?")) loop();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
