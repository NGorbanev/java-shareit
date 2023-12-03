package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;


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
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.debug("POST user received. user={}", userDto);
        return service.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable int userId, @RequestBody UserDto userDto) {
        log.debug("PATCH /userId received. UserId={}, updating user={}", userId, userDto);
        return service.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public Boolean deleteUser(@PathVariable int userId) {
        log.debug("DELETE /userId user received. UserId={}", userId);
        service.deleteUser(userId);
        return true;
    }

    @GetMapping()
    public Collection<UserDto> getAllUsers() {
        log.debug("GET user all received");
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable int userId) {
        log.debug("GET /userId received. UserId={} ", userId);
        return service.getUser(userId);
    }
}
