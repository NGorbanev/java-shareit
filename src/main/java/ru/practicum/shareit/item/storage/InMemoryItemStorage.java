package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryItemStorage implements ItemStorage {

    HashMap<Integer, Item> storage = new HashMap<>();
    int index = 0;

    @Override
    public Collection<Item> getAll() {
        return storage.values();
    }

    @Override
    public Optional<Item> getItemById(int itemId) {
        return Optional.ofNullable(storage.get(itemId));
    }

    @Override
    public Item addItem(Item item) {
        item.setId(++index);
        storage.put(index, item);
        return getItemById(index).isEmpty() ? null : item;
    }

    @Override
    public Item updateItem(Item item) {
        storage.put(item.getId(), item);
        return getItemById(index).isPresent() ? item : null;
    }

    @Override
    public boolean removeItem(Item item) {
        return storage.remove(item.getId()).equals(item);
    }

    @Override
    public boolean removeItemById(int itemId) {
        if (storage.get(itemId) != null) {
            storage.remove(itemId);
            return true;
        } else {
            throw new NotFoundException("item id=" + itemId);
        }
    }
}
