package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utils.UserMapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private User user = User.builder()
            .id(1)
            .name("Petya")
            .email("petya@sobaka.ru")
            .build();

    @Test
    public void shouldReturnUserById() {
        UserDto userDto = userService.addUser(UserMapper.toUserDto(user));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldThrowExceptionWhenUserIdIsWrongWhileDeleting() {
        EmptyResultDataAccessException exception = assertThrows(
                EmptyResultDataAccessException.class, () -> userService.deleteUser(100));
    }

    @Test
    public void shouldDeleteUser() {
        User victim = User.builder()
                .id(300)
                .name("Traktorist")
                .email("300@traktorist.org")
                .build();
        UserDto userDto = userService.addUser(UserMapper.toUserDto(victim));
        List<UserDto> userDtoList = (List<UserDto>) userService.getAllUsers();
        int size = userDtoList.size();
        userService.deleteUser(userDto.getId());
        userDtoList = (List<UserDto>) userService.getAllUsers();
        assertThat(userDtoList.size(), equalTo(size - 1));
    }

    @Test
    public void userUpdateTest() {
        UserDto userDto = userService.addUser(UserMapper.toUserDto(user));
        userDto.setName("UpdatedName");
        userDto.setEmail("Updated@email.org");
        UserDto updatedUserDto = userService.update(userDto.getId(), userDto);
        assertThat(updatedUserDto.getName(), equalTo("UpdatedName"));
        assertThat(updatedUserDto.getEmail(), equalTo("Updated@email.org"));
    }

    @Test
    public void shouldThrowExceptionWhenAddingUserWithEmailThatIsAlreadyRegistered() {
        userService.addUser(UserMapper.toUserDto(user));
        Assertions.assertThrows(ConflictException.class, () ->
                userService.addUser(UserDto.builder()
                        .name("Second one")
                        .email("petya@sobaka.ru")
                        .build()));
    }
}
