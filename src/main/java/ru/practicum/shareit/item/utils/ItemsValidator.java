package ru.practicum.shareit.item.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.utils.UserValidator;

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

    public boolean validateItem(Item item) {
        userValidator.validateUserById(item.getOwnerId());
        return true;
    }

    public boolean ownerMatch(Integer itemId, Integer userId) {
        if (itemId == null || userId == null) {
            throw new NullPointerException("User or Item is null");
        }
        userValidator.validateUserById(userId);
        Item inputItem = itemStorage.getItemById(itemId).orElseThrow(
                () -> new NotFoundException("Item id=" + itemId + " not found"));
        User inputUser = userStorage.getUserById(userId).orElseThrow(
                () -> new NotFoundException("User id " + userId + " not found"));
        User itemOwner = userStorage.getUserById(inputItem.getOwnerId()).orElseThrow(
                () -> new NotFoundException("Item owner was not found for item id=" + itemId));
        if (inputUser == itemOwner) {
            return true;
        } else {
            return false;
        }
    }
}
