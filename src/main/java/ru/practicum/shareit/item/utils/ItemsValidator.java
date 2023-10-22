package ru.practicum.shareit.item.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.utils.UserValidator;

@Component
public class ItemsValidator {
    //private final ItemStorage itemStorage;
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final UserValidator userValidator;

    @Autowired
    public ItemsValidator(ItemRepository itemStorage, UserRepository userStorage, UserValidator userValidator) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.userValidator = userValidator;
    }

    public boolean validateItem(Item item) {
        userValidator.validateUserById(item.getOwner().getId());
        return true;
    }

    public boolean ownerMatch(Integer itemId, Integer userId) {
        if (itemId == null || userId == null) {
            throw new NullPointerException("User or Item is null");
        }
        userValidator.validateUserById(userId);
        Item inputItem = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item id=" + itemId + " not found"));
        User inputUser = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("User id " + userId + " not found"));
        User itemOwner = userStorage.findById(inputItem.getOwner().getId()).orElseThrow(
                () -> new NotFoundException("Item owner was not found for item id=" + itemId));
        if (inputUser == itemOwner) {
            return true;
        } else {
            return false;
        }
    }
}
