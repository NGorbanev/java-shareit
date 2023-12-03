package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotAllowedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private User user = User.builder()
            .id(10)
            .name("Petya")
            .email("Petya@mail.onion")
            .build();
    private UserDto userDto1 = UserDto.builder()
            .id(11)
            .name("UserDto1")
            .email("user@dto.1")
            .build();
    private UserDto userDto2 = UserDto.builder()
            .id(12)
            .name("UserDto2")
            .email("user@dto.2")
            .build();

    private ItemDto itemDto1 = ItemDto.builder()
            .name("Item1")
            .description("SomeItem1")
            .available(true)
            .build();
    private ItemDto itemDto2 = ItemDto.builder()
            .name("Item2")
            .description("SomeItem2")
            .available(true)
            .build();
    private UserDto newUserDto;
    private ItemDto newItemDto;


    private List<ItemDto> itemDtoGenerator(int amount) {
        List<ItemDto> result = new ArrayList<>();
        for (int i = 1; i < amount; i++) {
            result.add(ItemDto.builder()
                    .id(i)
                    .name("Item" + i + " name")
                    .description("Item " + i + "description")
                    .available(true)
                    .build());
        }
        return result;
    }

    @BeforeEach
    public void beforeEach() {
        newUserDto = userService.addUser(userDto1);
        newItemDto = itemService.create(itemDto1, newUserDto.getId());

    }

    @Test
    public void getPageableItemsTest() {
        UserDto userDto = userService.addUser(UserDto.builder()
                .name("Ivan")
                .email("ya@van.ya")
                .build());
        for (ItemDto newItem: itemDtoGenerator(300)) {
            itemService.create(newItem, userDto.getId());
        }
        PageRequest pageRequest = PageRequest.of(0, 10);
        Assertions.assertEquals(10,
                itemService.getAllItemsOfUserPageable(userDto.getId(), pageRequest).size());
    }

    @Test
    public void itemCreationTest() {
        ItemDto returnedItemDto = itemService.get(newItemDto.getId(), newUserDto.getId());
        Assertions.assertEquals(returnedItemDto.getName(), itemDto1.getName());
    }

    @Test
    public void shouldReturnAnItemByIdOrThrowExceptionIfIdIsWrong() {
        Assertions.assertEquals(itemService.get(newItemDto.getId(), newUserDto.getId()).getName(), itemDto1.getName());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.get(100, 1));
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingItemNotByOwner() {
        UserDto userDto = userService.addUser(UserDto.builder()
                .name("Oleg")
                .email("oleg@petrovich.org")
                .build());
        Assertions.assertThrows(
                NotAllowedException.class, () -> itemService.update(newItemDto.getId(), newItemDto, userDto.getId()));
    }

    @Test
    public void updateItemsTest() {
        itemDto2.setId(0);
        ItemDto itemDto3 = itemService.update(newItemDto.getId(), itemDto2, newUserDto.getId());
        Assertions.assertEquals(itemDto3.getName(), itemDto2.getName());
    }

    @Test
    public void getAllItemsTest() {
        itemService.create(itemDto1, newUserDto.getId());
        itemService.create(itemDto2, newUserDto.getId());
        Assertions.assertEquals(itemService.getAllItemsOfUser(newUserDto.getId(), 0, 10).size(), 3);
    }

    @Test
    public void getAllItemsOfOwnerTest() {
        itemService.create(itemDto1, newUserDto.getId());
        itemService.create(itemDto2, newUserDto.getId());
        Assertions.assertEquals(itemService.getAllItemsOfUser(newUserDto.getId(), 0, 10).size(), 3);
    }

    @Test
    public void getItemBySearchTest() {
        itemDto2.setName("String for search");
        itemDto2.setId(0);
        itemService.create(itemDto2, newUserDto.getId());
        Assertions.assertEquals(1, itemService.search("String for search", 0, 10).size());
    }

    @Test
    public void createCommentTest() {
        ItemMapper itemMapper = new ItemMapper(bookingService, itemService);
        UserDto booker = userService.addUser(userDto2);
        IncomingBookingDto newBooking = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();
        BookingDto bookingDto = bookingService.create(newBooking, booker.getId());
        bookingService.update(bookingDto.getId(), newUserDto.getId(), true);
        CommentDto commentDto = CommentDto.builder()
                .text("New comment")
                .authorName(booker.getName())
                .item(itemMapper.toItem(itemService.get(newItemDto.getId(), newUserDto.getId())))
                .created(LocalDateTime.now().plusSeconds(5))
                .build();
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        itemService.addComment(commentDto, newItemDto.getId(), booker.getId());
        Assertions.assertEquals(1, itemService.getCommentsByItemId(newItemDto.getId()).size());
    }

    @Test
    public void shouldThrowExceptionWhenCommenterIsNotBooker() {
        ItemMapper itemMapper = new ItemMapper(bookingService, itemService);
        UserDto notBooker = userService.addUser(userDto2);
        CommentDto commentDto = CommentDto.builder()
                .text("New comment")
                .authorName(notBooker.getName())
                .item(itemMapper.toItem(itemService.get(newItemDto.getId(), newUserDto.getId())))
                .created(LocalDateTime.now().plusSeconds(5))
                .build();
        Assertions.assertThrows(ValidatonException.class,
                () -> itemService.addComment(commentDto, newItemDto.getId(), notBooker.getId()));
    }

    @Test
    public void getItemNotByOwnerTest() {
        UserDto notOwner = userService.addUser(userDto2);
        UserDto owner = newUserDto;
        UserDto commenter = userService.addUser(UserDto.builder()
                .name("Commenter")
                .email("usefullCommnet@spam.com")
                .build());
        IncomingBookingDto newBooking = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();
        BookingDto bookingDto = bookingService.create(newBooking, commenter.getId());
        Assertions.assertTrue(Optional.ofNullable(itemService.get(newItemDto.getId(), notOwner.getId())).isPresent());
        Assertions.assertNull(itemService.get(newItemDto.getId(), notOwner.getId()).getNextBooking());
        Assertions.assertNotNull(itemService.get(newItemDto.getId(), owner.getId()).getNextBooking());
        System.out.println(itemService.get(newItemDto.getId(), notOwner.getId()).toString());
    }

    @Test
    public void deleteItemTest() {
        UserDto notOwner = userService.addUser(UserDto.builder()
                .name("SomeName")
                .email("some@email.ru")
                .build());
        UserDto owner = newUserDto;
        Assertions.assertThrows(NotAllowedException.class,
                () -> itemService.delete(newItemDto.getId(), notOwner.getId()));
        itemService.delete(newItemDto.getId(), owner.getId());
        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.get(newItemDto.getId(), owner.getId()));
    }
}
