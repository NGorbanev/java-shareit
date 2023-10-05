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
    public Item update(int itemId, Item itemTransferName, int user) {
        if (!validator.ownerMatch(itemId, user)) {
            throw new NotAllowedException(itemTransferName, user);
        }
        Optional<ItemDto> itemDto = itemStorage.getItemById(itemId);
        if (itemDto.isPresent()) {
            ItemDto itemDtoForUpdate = ItemMapper.toItemDto(itemTransferName);
            itemDtoForUpdate.setOwnerId(itemDto.get().getOwnerId());
            itemDtoForUpdate.setId(itemId);
            if (itemTransferName.getName() != null) {
                itemDtoForUpdate.setName(itemTransferName.getName());
            } else {
                itemDtoForUpdate.setName(itemDto.get().getName());
            }
            if (itemTransferName.getDescription() != null) {
                itemDtoForUpdate.setDescription(itemTransferName.getDescription());
            } else {
                itemDtoForUpdate.setDescription(itemDto.get().getDescription());
            }
            if (itemTransferName.getAvailable() != null) {
                itemDtoForUpdate.setAvailable(itemTransferName.getAvailable());
            } else {
                itemDtoForUpdate.setAvailable(itemDto.get().getAvailable());
            }
            if (Integer.valueOf(itemTransferName.getRequestId()) != null) {
                itemDtoForUpdate.setRequestId(itemTransferName.getRequestId());
            } else {
                itemDtoForUpdate.setRequestId(itemDto.get().getRequestId());
            }
            return ItemMapper.toItem(itemStorage.updateItem(itemDtoForUpdate));
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
