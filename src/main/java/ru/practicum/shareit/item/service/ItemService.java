package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection getAllItems();

    Collection getAllItemsOfUser(int userId);

    ItemDto create(ItemDto itemDto, Integer userId);

    ItemDto get(int itemId);

    ItemDto update(int itemId, ItemDto itemDto, int userId);

    boolean delete(int itemId, int userId);

    Collection search(String text);
}
