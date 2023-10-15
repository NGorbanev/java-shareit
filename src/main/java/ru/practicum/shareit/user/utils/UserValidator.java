package ru.practicum.shareit.user.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
public class UserValidator {
    private final UserStorage userStorage;

    @Autowired
    public UserValidator(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean validateUserDto(UserDto userDto) {
        for (User u : userStorage.getAllUsers()) {
            if (u.getEmail().equals(userDto.getEmail())) {
                throw new ConflictException("Email is already registered");
            }
        }
        return true;
    }

    public boolean validateUserById(int id) {
        if (!userStorage.getUserById(id).isEmpty()) {
            return true;
        } else {
            throw new NotFoundException(String.format("User id=%s not found", id));
        }
    }
}
