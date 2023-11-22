package ru.practicum.shareit.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) {
        super(String.format("Object not found: %s", msg));
    }
}
