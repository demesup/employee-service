package org.demesup;

public class NotEntityClassException extends RuntimeException{
    public NotEntityClassException(Class<?> c) {
        super(c.getSimpleName()+" is not annotated with jakarta.Entity");
    }
}
