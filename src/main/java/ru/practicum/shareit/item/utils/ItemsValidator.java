package ru.practicum.shareit.item.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.utils.UserValidator;

import java.util.Optional;

@Component
public class ItemsValidator {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final UserValidator userValidator;

    @Autowired
    public ItemsValidator(ItemStorage itemStorage, UserStorage userStorage, UserValidator userValidator) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.userValidator = userValidator;
    }

    public boolean validateItemDto(ItemDto itemDto) {
        userValidator.validateUserById(itemDto.getOwnerId());
        if (itemDto.getName().isEmpty() || itemDto.getName().isBlank()) {
            throw new ValidatonException(itemDto, "Name field is empty");
        }
        if (itemDto.getDescription().isEmpty() || itemDto.getDescription().isBlank()) {
            throw new ValidatonException(itemDto, "Description field is empty");
        }
        return true;
    }

    public boolean ownerMatch(Integer itemId, Integer userId) {
        if (itemId == null || userId == null) {
            throw new NullPointerException("User or Item is null");
        }
        userValidator.validateUserById(userId);
        Optional<ItemDto> inputItem = itemStorage.getItemById(itemId);
        if (inputItem.isPresent()) {
            Optional<UserDto> inputUser = userStorage.getUserById(userId);
            if (inputUser.isPresent()) {
                Optional<UserDto> itemOwner = userStorage.getUserById(inputItem.get().getOwnerId());
                if (itemOwner.isPresent()) {
                    if (userId.equals(itemOwner.get().getId())) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    throw new NotFoundException("Item owner was not found for item id=" + itemId);
                }
            } else {
                throw new NotFoundException("User id " + userId + " not found");
            }
        } else {
            throw new NotFoundException("Item id=" + itemId + " not found");
        }
    }
}
