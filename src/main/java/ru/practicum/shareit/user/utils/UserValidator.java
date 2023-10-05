package ru.practicum.shareit.user.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
public class UserValidator {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public UserValidator(UserStorage userStorage, ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public boolean validateUserDto(UserDto userDto, UserValidatorSettings type) {
        type = UserValidatorSettings.EMAIL_CHECK;
        switch (type) {
            case FULL_CHECK:
                if (userDto.getName().isBlank() || userDto.getName().isEmpty()) {
                    throw new ValidatonException(userDto, "Name field is empty or blank");
                }
                if (userDto.getEmail().isBlank() || userDto.getEmail().isEmpty()) {
                    throw new ValidatonException(userDto, "Email field is empty or blank");
                }
                validateUserDto(userDto, UserValidatorSettings.EMAIL_CHECK);
                break;
            case EMAIL_CHECK:
                for (UserDto u : userStorage.getAllUsers()) {
                    if (u.getEmail().equals(userDto.getEmail())) {
                        //throw new ValidatonException(userDto, "Email is already registered");
                        throw new ConflictException("Email is already registered");
                    }
                }
                break;
            case ITEM_CHECK:

                break;

        }
        return true;
    }

    public boolean validateUserById(int id) {
        if (!userStorage.getUserById(id).isEmpty()) {
            return validateUserDto(userStorage.getUserById(id).get(), UserValidatorSettings.ITEM_CHECK);
        } else {
            throw new NotFoundException(String.format("User id=%s not found", id));
        }
    }
}
