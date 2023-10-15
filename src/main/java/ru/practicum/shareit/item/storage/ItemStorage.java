package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Collection<Item> getAll();

    Optional<Item> getItemById(int itemId);

    Item addItem(Item item);

    Item updateItem(Item item);

    boolean removeItem(Item item);

    boolean removeItemById(int itemId);
}
