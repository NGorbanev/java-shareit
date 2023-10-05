package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.utils.UserMapper;
import ru.practicum.shareit.user.utils.UserValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserValidator validator;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, UserValidator validator) {
        this.userStorage = userStorage;
        this.validator = validator;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        List<UserDto> users = new ArrayList<>();
        for (User u : userStorage.getAllUsers()) {
            users.add(UserMapper.toUserDto(u));
        }
        return users;
    }

    @Override
    public UserDto getUser(int id) {
        UserDto userDto;
        if (userStorage.getUserById(id).isPresent()) {
            userDto = UserMapper.toUserDto(userStorage.getUserById(id).get());
            return userDto;
        }
        throw new NotFoundException("User id=" + id + " not found");
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (validator.validateUserDto(userDto)) {
            return UserMapper.toUserDto(userStorage.addUser(UserMapper.toUser(userDto)));
        }
        throw new ValidatonException(userDto, "Validation failed");
    }

    @Override
    public UserDto update(int id, UserDto userDto) {
        if (userStorage.getUserById(id).isPresent()) {
            User oldUser = userStorage.getUserById(id).get();
            if (userDto.getName() == null) {
                userDto.setName(oldUser.getName());
            }
            if (userDto.getEmail() == null) {
                userDto.setEmail(oldUser.getEmail());
            } else if (!userDto.getEmail().equals(oldUser.getEmail())) {
                validator.validateUserDto(userDto);
            }
            User updatedUser = UserMapper.toUser(userDto);
            return UserMapper.toUserDto(userStorage.updateUser(id, updatedUser));
        } else {
            throw new NotFoundException(userDto);
        }
    }

    @Override
    public boolean deleteUser(int id) {
        if (userStorage.getUserById(id).isPresent()) {
            return userStorage.deleteUserById(id);
        } else {
            throw new NotFoundException(String.format("User id=%s not found", id));
        }
    }
}
