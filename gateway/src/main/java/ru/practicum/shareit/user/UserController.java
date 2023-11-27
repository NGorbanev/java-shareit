package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserGateClient userGateClient;

    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserDto userDto) {
        log.debug("POST request received. user={}", userDto);
        return userGateClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Min(1) long userId, @RequestBody UserDto userDto) {
        log.debug("PATCH request received. UserId={} updating user={}", userId, userDto);
        return userGateClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) long userId) {
        log.debug("DELETE request received. UserId={}", userId);
        userGateClient.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        log.debug("GET all users request received");
        return userGateClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Min(1) long userId) {
        log.debug("GET request received. UserId={}", userId);
        return userGateClient.getUser(userId);
    }
}
