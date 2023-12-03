package ru.practicum.shareit.booking.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utils.UserMapper;

@Component
public class BookingMapper {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(UserService userService, ItemService itemService, ItemMapper im) {
        this.userService = userService;
        this.itemService = itemService;
        this.itemMapper = im;
    }

    public BookingDto toBookingDto(Booking booking) {
        if (booking != null) {
            return BookingDto.builder()
                    .id(booking.getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .item(itemMapper.toItemDto(booking.getItem()))
                    .booker(UserMapper.toUserDto(booking.getBooker()))
                    .status(booking.getStatus())
                    .build();
        } else {
            throw new ValidatonException("Unable to convert Booking to BookingDto");
        }
    }

    public ShortBookingInfo toShortBookingInfo(Booking booking) {
        if (booking != null) {
            return ShortBookingInfo.builder()
                    .id(booking.getId())
                    .bookerId(booking.getBooker().getId())
                    .start(booking.getStart())
                    .end(booking.getEnd())
                    .build();
        } else {
            return null;
        }
    }

    public Booking toBooking(IncomingBookingDto input, int bookerId) {
        return Booking.builder()
                .start(input.getStart())
                .end(input.getEnd())
                .item(itemMapper.toItem(itemService.get(input.getItemId(), bookerId)))
                .booker(UserMapper.toUser(userService.getUser(bookerId)))
                .status(BookingStatus.WAITING)
                .build();
    }
}
