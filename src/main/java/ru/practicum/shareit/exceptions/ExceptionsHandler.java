package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("Object not found exception: {}", e.getMessage());
        return new ErrorResponse(String.format("Object not found. Cause: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNPE(final NullPointerException e) {
        log.warn("NPE: {}", e.getMessage());
        return new ErrorResponse(String.format("NPE achieved. Cause: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidatonException e) {
        log.warn("Validate exception: {}", e.getMessage());
        return new ErrorResponse(String.format("Validation error: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownStateException(UnknownStateException e) {
        log.warn("Unknown state exception: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        log.warn("Conflict: {}",e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSpringvalidatorExceptoions(MethodArgumentNotValidException e) {
        log.warn("Validate exception: {}", e.getMessage());
        return new ErrorResponse("Validation error: required fields are absent");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoHandleCase(final MissingRequestHeaderException e) {
        log.warn("Wrong header at reques: {}", e.getMessage());
        return new ErrorResponse(
                "Required request header 'X-Sharer-User-id' for method parameter type int is not present");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNotAlowedException(final NotAllowedException e) {
        log.warn("Not allowed exception: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintException(final ConstraintViolationException e) {
        log.warn("Constraint exception: " + e.getMessage());
        return new ErrorResponse("Constraint exception " + e.getMessage());
    }
}
