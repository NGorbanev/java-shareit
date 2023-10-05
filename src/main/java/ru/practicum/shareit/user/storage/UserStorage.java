package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    Optional<User> getUserById(int userId);

    boolean deleteUserById(int userId);

    Collection<User> getAllUsers();

    User updateUser(int userId, User user);
}
