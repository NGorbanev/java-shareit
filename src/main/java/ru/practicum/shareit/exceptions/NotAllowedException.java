package ru.practicum.shareit.exceptions;

import ru.practicum.shareit.item.dto.ItemDto;

public class NotAllowedException extends RuntimeException {
    public NotAllowedException(ItemDto item, int userId) {
        super(String.format("User id=%s is not the owner of item id=%s", userId, item.getId()));
    }

    public NotAllowedException(String msg) {
        super(msg);
    }
}
