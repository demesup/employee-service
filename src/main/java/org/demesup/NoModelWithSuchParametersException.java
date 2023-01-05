package org.demesup;

public class NoModelWithSuchParametersException extends RuntimeException {
    public NoModelWithSuchParametersException(String string) {
        super("No model with " + string);
    }

    public NoModelWithSuchParametersException() {
        super("Model not found");
    }
}
