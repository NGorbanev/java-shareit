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
        return service.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable int userId, @RequestBody UserDto userDto) {
        return service.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUser(@PathVariable int userId) {
        return service.deleteUser(userId);
    }

    @GetMapping()
    public Collection<User> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        return service.getUser(userId);
    }
}
