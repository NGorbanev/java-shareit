package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidatonException extends RuntimeException {
    public ValidatonException(String msg) {
        super(msg);
    }

    public ValidatonException(Object obj, String msg) {
        super(String.format("Object %s validation failed. Cause: %s", obj.getClass().getSimpleName(), msg));
    }
}
