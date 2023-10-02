package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Collection<ItemDto> getAll();
    Optional<ItemDto> getItemById(int itemId);
    ItemDto addItem (ItemDto itemDto);
    ItemDto updateItem (ItemDto itemDto);
    boolean removeItem (ItemDto itemDto);
    boolean removeItemById (int itemId);
}
