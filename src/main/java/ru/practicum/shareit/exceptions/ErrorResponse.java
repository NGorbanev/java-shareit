package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        log.error("Exception generated: {}", error);
        this.error = error;
    }

    public String getError() {
        log.error("Exception generated: {}", error);
        return error;
    }
}