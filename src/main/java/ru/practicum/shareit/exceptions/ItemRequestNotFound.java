package ru.practicum.shareit.exceptions;

public class ItemRequestNotFound extends IllegalArgumentException{
    public ItemRequestNotFound(String msg) {
        super(msg);
    }
}
