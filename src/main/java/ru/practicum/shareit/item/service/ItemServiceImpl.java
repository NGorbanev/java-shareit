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
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemsValidator validator;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage, ItemsValidator validator) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.validator = validator;
    }

    @Override
    public Collection<ItemDto> getAllItems() {
        ArrayList<Item> itemDtos = new ArrayList<>(itemStorage.getAll());
        Collection<ItemDto> result = new ArrayList<>();
        for (Item i : itemDtos) {
            result.add(ItemMapper.toItemDto(i));
        }
        return result;
    }

    @Override
    public Collection<ItemDto> getAllItemsOfUser(int userId) {
        List<Item> items = itemStorage.getAll().stream()
                .filter(itemDto -> itemDto.getOwnerId() == userId)
                .collect(Collectors.toList());
        ArrayList<ItemDto> result = new ArrayList<>();
        for (Item i : items) {
            result.add(ItemMapper.toItemDto(i));
        }
        return result;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        validator.validateItem(item);
        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto get(int itemId) {
        Optional<Item> item = itemStorage.getItemById(itemId);
        if (item.isPresent()) {
            return ItemMapper.toItemDto(item.get());
        } else {
            throw new NotFoundException(String.format("Item id=%s not found", itemId));
        }
    }

    @Override
    public ItemDto update(int itemId, ItemDto itemTransferName, int user) {
        if (!validator.ownerMatch(itemId, user)) {
            throw new NotAllowedException(itemTransferName, user);
        }
        Optional<Item> item = itemStorage.getItemById(itemId);
        if (item.isPresent()) {
            Item itemForUpdate = ItemMapper.toItem(itemTransferName);
            itemForUpdate.setOwnerId(item.get().getOwnerId());
            itemForUpdate.setId(itemId);
            if (itemTransferName.getName() != null) {
                itemForUpdate.setName(itemTransferName.getName());
            } else {
                itemForUpdate.setName(item.get().getName());
            }
            if (itemTransferName.getDescription() != null) {
                itemForUpdate.setDescription(itemTransferName.getDescription());
            } else {
                itemForUpdate.setDescription(item.get().getDescription());
            }
            if (itemTransferName.getAvailable() != null) {
                itemForUpdate.setAvailable(itemTransferName.getAvailable());
            } else {
                itemForUpdate.setAvailable(item.get().getAvailable());
            }
            if (Integer.valueOf(itemTransferName.getRequestId()) != null) {
                itemForUpdate.setRequestId(itemTransferName.getRequestId());
            } else {
                itemForUpdate.setRequestId(item.get().getRequestId());
            }
            return ItemMapper.toItemDto(itemStorage.updateItem(itemForUpdate));
        } else {
            throw new NotFoundException(itemTransferName);
        }
    }

    @Override
    public boolean delete(int itemId, int userId) {
        if (!validator.ownerMatch(itemId, userId)) {
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
    public Collection<ItemDto> search(String text) {
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
