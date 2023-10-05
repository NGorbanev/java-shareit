package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User postUser(@Valid @RequestBody UserDto userDto) {
        log.debug(String.format("POST request received. user=%s", userDto));
        return service.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable int userId, @RequestBody UserDto userDto) {
        log.debug(String.format("PATCH request received. UserId=%s updating user=%s", userId, userDto));
        return service.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUser(@PathVariable int userId) {
        log.debug(String.format("DELETE request received. UserId=%s", userId));
        return service.deleteUser(userId);
    }

    @GetMapping()
    public Collection<User> getAllUsers() {
        log.debug("GET all users request received");
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        log.debug(String.format("GET request received. UserId=%s ", userId));
        return service.getUser(userId);
    }
}
