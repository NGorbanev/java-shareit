package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Collection<Item> getAllItems();

    Collection<Item> getAllItemsOfUser(int userId);

    Item create(ItemDto itemDto, Integer userId);

    Item get(int itemId);

    Item update(int itemId, Item item, int userId);

    boolean delete(int itemId, int userId);

    Collection<Item> search(String text);
}
