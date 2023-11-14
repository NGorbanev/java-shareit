package ru.practicum.shareit.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Object obj) {
        super(String.format("Object %s not found", obj.getClass().getName()));
    }

    public NotFoundException(String msg) {
        super(String.format("Object not found: %s", msg));
    }
}
