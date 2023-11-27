package shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.utils.UserValidator;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    @Autowired
    private UserValidator validator;
    private UserService userService;
    private UserDto userDto = UserDto.builder()
            .id(1)
            .name("Petya")
            .email("petya@valenok.ru")
            .build();

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(validator, mockUserRepository);
    }

    @Test
    public void throwExceptionWhenGetUserWithWrongId() {
        when(mockUserRepository.findById(any(int.class)))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(300));
    }

    @Test
    public void throwExceptionWhenAddingUserWithExistingEmail() {
        when(mockUserRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));
        final ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> userService.addUser(userDto));
    }

    @Test
    public void shouldReturnUserById() {
        User mockUser = User.builder()
                .id(1)
                .name("Vasya")
                .email("vasya@ne-petya.com")
                .build();
        when(mockUserRepository.findById(any(int.class)))
                .thenReturn(Optional.of(mockUser));
        UserDto userDto2 = userService.getUser(1);
        verify(mockUserRepository, Mockito.times(1)).findById(1);
        Assertions.assertEquals(userDto2.getName(), mockUser.getName());
        Assertions.assertEquals(userDto2.getEmail(), mockUser.getEmail());
    }

    @Test
    public void validationByIdSholdFail() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> validator.validateUserById(100)
        );
    }
}
