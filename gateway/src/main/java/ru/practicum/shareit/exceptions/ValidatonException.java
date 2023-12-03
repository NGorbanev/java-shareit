package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidatonException extends RuntimeException {
    public ValidatonException(String msg) {
        super(msg);
    }
}
