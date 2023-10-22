package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto getUser(int id);

    UserDto addUser(UserDto userDto);

    UserDto update(int id, UserDto userDto);

    void deleteUser(int id);
}
