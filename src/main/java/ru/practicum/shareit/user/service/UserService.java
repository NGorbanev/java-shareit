package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();

    User getUser(int id);

    User addUser(UserDto userDto);

    User update(int id, UserDto userDto);

    boolean deleteUser(int id);
}
