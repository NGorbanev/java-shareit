package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotAllowedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.item.utils.ItemsValidator;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService{
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemsValidator validator;

    @Autowired
    public ItemServiceImpl (ItemStorage itemStorage, UserStorage userStorage, ItemsValidator validator) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.validator = validator;
    }

    @Override
    public Collection<Item> getAllItems() {
        ArrayList<ItemDto> itemDtos = new ArrayList<>(itemStorage.getAll());
        Collection<Item> result = new ArrayList<>();
        for (ItemDto i : itemDtos) {
            result.add(ItemMapper.toItem(i));
        }
        return result;
    }

    @Override
    public Collection<Item> getAllItemsOfUser(int userId) {
        List<ItemDto> itemDtos = itemStorage.getAll().stream()
                .filter(itemDto -> itemDto.getOwnerId() == userId)
                .collect(Collectors.toList());
        ArrayList<Item> result = new ArrayList<Item>();
        for (ItemDto i : itemDtos) {
            result.add(ItemMapper.toItem(i));
        }
        return result;
    }

    @Override
    public Item create(ItemDto itemDto, Integer userId) {
        //if (itemDto == null || userId == null) {
        //    throw new NullPointerException("User id or item is null");
        //}
        itemDto.setOwnerId(userId);
        validator.validateItemDto(itemDto);
        return ItemMapper.toItem(itemStorage.addItem(itemDto));
    }

    @Override
    public Item get(int itemId) {
        Optional<ItemDto> itemDto = itemStorage.getItemById(itemId);
        if (itemDto.isPresent()) {
            return ItemMapper.toItem(itemDto.get());
        } else {
            throw new NotFoundException(String.format("Item id=%s not found", itemId));
        }
    }

    @Override
    public Item update(int itemId, Item item, int user) {
        if (!validator.ownerMatch(itemId, user)) {
            throw new NotAllowedException(item, user);
        }
        Optional<ItemDto> itemDto = itemStorage.getItemById(itemId);
        if (itemDto.isPresent()) {
            ItemDto itemForUpdate = ItemMapper.toItemDto(item);
            itemForUpdate.setOwnerId(itemDto.get().getOwnerId());
            itemForUpdate.setId(itemId);
            if (item.getName() != null) {
                itemForUpdate.setName(item.getName());
            } else {
                itemForUpdate.setName(itemDto.get().getName());
            }
            if (item.getDescription() != null) {
                itemForUpdate.setDescription(item.getDescription());
            } else {
                itemForUpdate.setDescription(itemDto.get().getDescription());
            }
            if (item.getAvailable() != null) {
                itemForUpdate.setAvailable(item.getAvailable());
            } else {
                itemForUpdate.setAvailable(itemDto.get().getAvailable());
            }
            if (Integer.valueOf(item.getRequestId()) != null) {
                itemForUpdate.setRequestId(item.getRequestId());
            } else {
                itemForUpdate.setRequestId(itemDto.get().getRequestId());
            }
            return ItemMapper.toItem(itemStorage.updateItem(itemForUpdate));
        } else {
            throw new NotFoundException(item);
        }
    }

    @Override
    public boolean delete(int itemId, int userId) {
        if (! validator.ownerMatch(itemId, userId)) {
            throw new NotAllowedException(
                    String.format("User id=%s is not allowed to delete item id=%s", userId, itemId));
        }
        if (itemStorage.removeItemById(itemId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Collection<Item> search(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return getAllItems().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toUpperCase().contains(text.toUpperCase()) ||
                        item.getDescription().toUpperCase().contains(text.toUpperCase())))
                .collect(Collectors.toList());
    }
}
