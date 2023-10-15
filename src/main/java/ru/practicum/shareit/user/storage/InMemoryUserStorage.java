package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    HashMap<Integer, User> storage = new HashMap<>();
    int index = 0;

    @Override
    public User addUser(User user) {
        user.setId(++index);
        storage.put(index, user);
        return getUserById(index).isPresent() ? user : null;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public boolean deleteUserById(int userId) {
        if (storage.remove(userId) == null) {
            throw new NotFoundException(String.format("User id=%s was not found", userId));
        } else {
            return true;
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return storage.values();
    }

    @Override
    public User updateUser(int userId, User user) {
        User oldUser = storage.get(userId);
        if (oldUser == null) {
            throw new NotFoundException(String.format(user.toString(), userId));
        } else {
            user.setId(userId);
            if (user.getName() == null) {
                user.setName(oldUser.getName());
            }
            if (user.getEmail() == null) {
                user.setEmail(oldUser.getEmail());
            }
            storage.put(userId, user);
            return storage.get(userId);
            }
        }
    }
