package ru.practicum.shareit.exceptions;

import ru.practicum.shareit.item.model.Item;

public class NotAllowedException extends RuntimeException {
    public NotAllowedException(Item item, int userId) {
        super(String.format("User id=%s is not the owner of item id=%s", userId, item.getId()));
    }

    public NotAllowedException(String msg) {
        super(msg);
    }
}
