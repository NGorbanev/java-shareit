package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.utils.UserMapper;
import ru.practicum.shareit.user.utils.UserValidator;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserValidator validator;

    @Autowired
    public UserServiceImpl(UserValidator validator, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("getAllUsers method is servicing");
        List<UserDto> users = new ArrayList<>();
        for (User u : userRepository.findAll()) {
            users.add(UserMapper.toUserDto(u));
        }
        log.info("getAllUsers done");
        return users;
    }

    @Override
    public UserDto getUser(int id) {
        log.info("getUserId={} is servicing", id);
        UserDto userDto = UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found")));
        log.info("userId={} will be returned", id);
        return userDto;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        log.info("addUser '{}' method is servicing", userDto.getName());
        try {
            log.info("addUser performed");
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            log.warn("User with email '{}' is already registred", userDto.getEmail());
            throw new ConflictException("Email is already registered");
        }
    }

    @Override
    public UserDto update(int id, UserDto userDto) {
        log.info("User id={} update is servicing", id);
        User oldUser = userRepository.findById(id).orElseThrow();
        if (userDto.getName() == null) {
            userDto.setName(oldUser.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(oldUser.getEmail());
        } else if (!userDto.getEmail().equals(oldUser.getEmail())) {
            validator.validateUserDto(userDto);
        }
        User updatedUser = UserMapper.toUser(userDto);
        updatedUser.setId(id);
        log.info("User id={} will be updated", id);
        return UserMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Override
    public void deleteUser(int id) {
        log.info("Delete userId={} is servicing", id);
        userRepository.deleteById(id);
    }
}
