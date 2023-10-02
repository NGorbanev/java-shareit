package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    HashMap<Integer, UserDto> storage = new HashMap<>();
    int index = 0;

    @Override
    public UserDto addUser(UserDto userDto) {
        userDto.setId(++index);
        storage.put(index, userDto);
        return getUserById(index).isPresent() ? userDto : null;
    }

    @Override
    public Optional<UserDto> getUserById(int userId) {
        return Optional.ofNullable(storage.get(userId));

    }

    @Override
    public boolean deleteUserById(int userId) {
        if (storage.containsKey(userId)) {
            storage.remove(userId);
            return true;
        }
        throw new NotFoundException(String.format("User id=%s was not found", userId));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return storage.values();
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        if (storage.containsKey(userId)) {
            UserDto oldUser = storage.get(userId);
            userDto.setId(userId);
            if (userDto.getName() == null) {
                userDto.setName(oldUser.getName());
            }
            if (userDto.getEmail() == null) {
                userDto.setEmail(oldUser.getEmail());
            }
            storage.put(userId, userDto);
            return storage.get(userId);
        }
        throw new NotFoundException(String.format("User id=%s was not found", userId));
    }
}
