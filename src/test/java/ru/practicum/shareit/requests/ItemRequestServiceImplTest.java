package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.utils.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    private ItemRequestService itemRequestService;
    private ItemRequestMapper itemRequestMapper;


    @Mock
    private UserRepository mocUserRepository;

    private UserDto userDto = UserDto.builder()
            .name("User")
            .email("user@user.com")
            .build();

    private User user = User.builder()
            .name("User")
            .email("user@user.com")
            .build();

    @Test
    public void throwExceptionWhenItemRequestIsWithWrongId() {
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, itemRequestMapper, mocUserRepository);
        when(mockItemRequestRepository.findById(any(int.class))).thenReturn(Optional.empty());
        //when(mocUserService.getUser(any(int.class))).thenReturn(userDto);
        when(mocUserRepository.findById(any(int.class))).thenReturn(Optional.of(user));
        Assertions.assertThrows(ItemRequestNotFound.class,
                () -> itemRequestService.getItemRequestById(-1, -1));
    }
}
