package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private User user = User.builder()
            .id(10)
            .name("Petya")
            .email("Petya@mail.onion")
            .build();
    private UserDto userDto1 = UserDto.builder()
            .name("UserDto1")
            .email("user@dto.1")
            .build();
    private UserDto userDto2 = UserDto.builder()
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

    @Test
    public void throwExceptionWhenOwnerCreatesBookingOfHisItem() {
        UserDto ownerDto = userService.addUser(userDto1);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(5))
                .build();
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.create(incomingBookingDto, ownerDto.getId()));
    }

    @Test
    public void throwExceptionWhenCreatingBookingWithNotExistingUser() {
        UserDto ownerDto = userService.addUser(userDto1);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(5))
                .build();
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.create(incomingBookingDto, 300));
    }

    @Test
    public void throwExceptionWhenStartDateIsWrong() {
        UserDto ownerDto = userService.addUser(userDto1);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        UserDto newUserDto = userService.addUser(userDto2);
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().minusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(5))
                .build();
        Assertions.assertThrows(ValidatonException.class,
                () -> bookingService.create(incomingBookingDto, newUserDto.getId()));
    }

    @Test
    public void throwExceptionWhenEndDateIsBeforeStartDate() {
        UserDto ownerDto = userService.addUser(userDto1);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        UserDto newUserDto = userService.addUser(userDto2);
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusSeconds(5))
                .build();
        Assertions.assertThrows(ValidatonException.class,
                () -> bookingService.create(incomingBookingDto, newUserDto.getId()));
    }

    @Test
    public void throwExceptionWhenItemIsNotAvailableForBooking() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemDto1;
        newItemDto.setAvailable(false);
        newItemDto = itemService.create(newItemDto, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(5))
                .build();
        Assertions.assertThrows(ValidatonException.class,
                () -> bookingService.create(incomingBookingDto, newUserDto.getId()));
    }

    @Test
    public void throwExceptionIfNorBookerNorOwnerRequestsBooking() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        UserDto someone = userService.addUser(
                UserDto.builder()
                        .name("Anonymous")
                        .email("cheater@hacker.onion")
                        .build());
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        BookingDto bookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(bookingDto.getId(), someone.getId()));
    }

    @Test
    public void shouldThrowExceptionIfStatusIsIncorrect() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        Assertions.assertThrows(UnknownStateException.class,
                () -> bookingService.getBookingsPageable(
                        "SOMESTATUS", newUserDto.getId(), 0, null));
    }

    @Test
    public void shouldThrowExceptionIfStatusIsIncorrectRequestByOwner() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        Assertions.assertThrows(UnknownStateException.class,
                () -> bookingService.getBookingsOwner(
                        "SOMESTATUS", ownerDto.getId(), 0, null));
    }

    @Test
    public void shouldReturnBookingsByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        bookingService.create(incomingBookingDto1, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "ALL", newUserDto.getId(), 0, null);
        Assertions.assertEquals(2, testResult.size());
    }

    @Test
    public void shouldReturnBookingsByBookerAndSizeIsNOTNull() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        bookingService.create(incomingBookingDto1, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "ALL", newUserDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void shouldReturnBookingsByBookerAndSizeIsNullAndStatusIsWAITING() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        bookingService.create(incomingBookingDto1, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "WAITING", newUserDto.getId(), 0, null);
        Assertions.assertEquals(2, testResult.size());
    }

    @Test
    public void shouldReturnBookingsByBookerAndSizeIsNOTNullAndStatusIsWAITING() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        bookingService.create(incomingBookingDto1, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "WAITING", newUserDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void shouldReturnBookingsByBookerAndSizeIsNullAndStatusIsREJECTED() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto2, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        BookingDto bookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        bookingService.update(bookingDto.getId(), ownerDto.getId(), false);
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "REJECTED", newUserDto.getId(), 0, null);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void shouldReturnBookingsByBookerAndSizeIsNOTNullAndStatusIsREJECTED() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto2, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        BookingDto bookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        bookingService.update(bookingDto.getId(), ownerDto.getId(), false);
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "REJECTED", newUserDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getByOwnerAndStatusIsAllAndSizeIsNull() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        bookingService.create(incomingBookingDto1, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "ALL", ownerDto.getId(), 0, null);
        Assertions.assertEquals(2, testResult.size());
    }

    @Test
    public void getByOwnerAndStatusIsAllAndSizeIsNotNull() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        bookingService.create(incomingBookingDto1, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "ALL", ownerDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getByOwnerAndStatusIsWaitingAndSizeIsNull() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        bookingService.create(incomingBookingDto1, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "WAITING", ownerDto.getId(), 0, null);
        Assertions.assertEquals(2, testResult.size());
    }

    @Test
    public void getByOwnerAndStatusIsWaitingAndSizeIsNotNull() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        bookingService.create(incomingBookingDto1, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "WAITING", ownerDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getByOwnerAndStatusIsRejectedAndSizeIsNull() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto1, newUserDto.getId());
        bookingService.update(testBookingDto.getId(), ownerDto.getId(), false);
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "REJECTED", ownerDto.getId(), 0, null);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getByOwnerAndStatusIsRejectedAndSizeIsNotNull() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        bookingService.create(incomingBookingDto, newUserDto.getId());
        IncomingBookingDto incomingBookingDto1 = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(6))
                .end(LocalDateTime.now().plusSeconds(16))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto1, newUserDto.getId());
        bookingService.update(testBookingDto.getId(), ownerDto.getId(), false);
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "REJECTED", ownerDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getBookingInCancelledStatus() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        bookingService.update(testBookingDto.getId(), newUserDto.getId(), false);
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "CANCELLED", newUserDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getBookingInCancelledStatusByOwner() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(15))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        bookingService.update(testBookingDto.getId(), newUserDto.getId(), false);
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "CANCELLED", ownerDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getBookingInFutureStatus() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        bookingService.update(testBookingDto.getId(), ownerDto.getId(), true);
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "FUTURE", newUserDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getBookingInFutureStatusByOwner() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "FUTURE", ownerDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getBookingInPastStatus() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        bookingService.update(testBookingDto.getId(), ownerDto.getId(), true);
        try {
            sleep(3000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "PAST", newUserDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getBookingInPastStatusByOwner() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        try {
            sleep(3000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "PAST", ownerDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getBookingInCurrentStatus() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(5))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        bookingService.update(testBookingDto.getId(), ownerDto.getId(), true);
        try {
            sleep(2000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        List<BookingDto> testResult = bookingService.getBookingsPageable(
                "CURRENT", newUserDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }

    @Test
    public void getBookingInCurrentStatusByOwner() {
        UserDto ownerDto = userService.addUser(userDto1);
        UserDto newUserDto = userService.addUser(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(5))
                .build();
        BookingDto testBookingDto = bookingService.create(incomingBookingDto, newUserDto.getId());
        try {
            sleep(2000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        List<BookingDto> testResult = bookingService.getBookingsOwner(
                "CURRENT", ownerDto.getId(), 0, 1);
        Assertions.assertEquals(1, testResult.size());
    }
}
