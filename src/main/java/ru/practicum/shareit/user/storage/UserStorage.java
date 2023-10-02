package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    UserDto addUser(UserDto userDto);
    Optional<UserDto> getUserById(int userId);
    boolean deleteUserById(int userId);
    Collection<UserDto> getAllUsers();
    UserDto updateUser(int userId, UserDto userDto);
}
