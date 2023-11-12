package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private UserDto userDto1 = UserDto.builder()
            .name("Valera")
            .email("valera@tvoevremya.nastalo")
            .build();
    private UserDto userDto2 = UserDto.builder()
            .name("Vinnie")
            .email("vinnie@pooh.mdvd")
            .build();

    private ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("Test itemRequest")
            .requester(userDto1)
            .created(LocalDateTime.of(2023, 12, 13, 14, 15))
            .build();

    @Test
    public void shouldCreateRequest() {
        UserDto newUserDto = userService.addUser(userDto1);
        ItemRequestDto requestResult = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2023, 12, 13, 14, 15));
        Assertions.assertEquals(itemRequestDto.getDescription(), requestResult.getDescription());
    }

    @Test
    public void throwExceptionWhenRequestIsCreatedWithWrongId() {
        UserDto newUserDto = userService.addUser(userDto1);
        Assertions.assertThrows(ItemRequestNotFound.class,
                () -> itemRequestService.getItemRequestById(100, newUserDto.getId()));
    }

    @Test
    public void throwExceptionWhenCreatedWithWrongUserId() {
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, 100, LocalDateTime.now()));
    }

    @Test
    public void getAllItemRequestsWithSizeNotNullAndNull() {
        UserDto userDto1 = userService.addUser(this.userDto1);
        UserDto userDto2 = userService.addUser(this.userDto2);
        itemRequestService.create(itemRequestDto, userDto2.getId(),
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        itemRequestService.create(itemRequestDto, userDto2.getId(),
                LocalDateTime.of(2024, 1, 2, 3, 4, 5));
        Assertions.assertEquals(2, itemRequestService.getAllItemRequests(userDto1.getId(),
                0, 3).size());
        Assertions.assertEquals(2, itemRequestService.getAllItemRequests(userDto1.getId(),
                0, null).size());
    }

    @Test
    public void getOwnItemRequests() {
        UserDto userDto2 = userService.addUser(this.userDto2);
        itemRequestService.create(itemRequestDto, userDto2.getId(),
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        itemRequestService.create(itemRequestDto, userDto2.getId(),
                LocalDateTime.of(2024, 1, 2, 3, 4, 5));
        Assertions.assertEquals(2, itemRequestService.getOwnItemRequests(userDto2.getId()).size());
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getOwnItemRequests(100));
    }

    @Test
    public void getItemRequestByRequestId() {
        UserDto userDto = userService.addUser(userDto1);
        ItemRequestDto newItemRequestDto = itemRequestService.create(itemRequestDto, userDto.getId(),
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        ItemRequestDto result = itemRequestService.getItemRequestById(newItemRequestDto.getId(),
                userDto.getId());
        Assertions.assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }
}
