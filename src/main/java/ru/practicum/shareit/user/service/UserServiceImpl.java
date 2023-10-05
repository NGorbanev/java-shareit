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
import ru.practicum.shareit.user.utils.UserValidatorSettings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
    public Collection getAllUsers() {
        List<User> users = new ArrayList<>();
        for (UserDto u : userStorage.getAllUsers()) {
            users.add(UserMapper.toUser(u));
        }
        return users;
    }

    @Override
    public User getUser(int id) {
        User user;
        if (userStorage.getUserById(id).isPresent()) {
            user = UserMapper.toUser(userStorage.getUserById(id).get());
            return user;
        }
        throw new NotFoundException("User id=" + id + " not found");
    }

    @Override
    public User addUser(UserDto userDto) {
        if (validator.validateUserDto(userDto, UserValidatorSettings.EMAIL_CHECK)) {
            return UserMapper.toUser(userStorage.addUser(userDto));
        }
        throw new ValidatonException(userDto, "Validation failed");
    }

    @Override
    public User update(int id, UserDto userDto) {
        if (userStorage.getUserById(id).isPresent()) {
            UserDto oldUser = userStorage.getUserById(id).get();
            if (userDto.getName() == null) {
                userDto.setName(oldUser.getName());
            }
            if (userDto.getEmail() == null) {
                userDto.setEmail(oldUser.getEmail());
            } else if (!userDto.getEmail().equals(oldUser.getEmail())) {
                validator.validateUserDto(userDto, UserValidatorSettings.EMAIL_CHECK);
            }
            return UserMapper.toUser(userStorage.updateUser(id, userDto));
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
