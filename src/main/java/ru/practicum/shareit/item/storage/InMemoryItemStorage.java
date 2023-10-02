package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryItemStorage implements ItemStorage {

    HashMap<Integer, ItemDto> storage = new HashMap<>();
    int index = 0;

    @Override
    public Collection<ItemDto> getAll() {
        return storage.values();
    }

    @Override
    public Optional<ItemDto> getItemById(int itemId) {
        return Optional.ofNullable(storage.get(itemId));
    }

    @Override
    public ItemDto addItem(ItemDto itemDto) {
        itemDto.setId(++index);
        storage.put(index, itemDto);
        return getItemById(index).isEmpty() ? null : itemDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {
        storage.put(itemDto.getId(), itemDto);
        return getItemById(index).isPresent() ? itemDto : null;
    }

    @Override
    public boolean removeItem(ItemDto itemDto) {
        return storage.remove(itemDto.getId()).equals(itemDto);
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
