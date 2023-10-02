package ru.practicum.shareit.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException (Object obj) {
        super(String.format("Object %s not found %s", obj.getClass().getName()));
    }
    public NotFoundException (String msg) {
        super(String.format("Object not found: %s", msg));
    }

    public NotFoundException (Object object, String msg) {
        super(String.format("Object %s not found %s. Details: ", object.getClass().getName(), msg));
    }
}
