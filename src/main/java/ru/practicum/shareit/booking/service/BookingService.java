package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingInfo;

import java.util.List;

public interface BookingService {
    BookingDto create(IncomingBookingDto bookingDto, int bookerId);

    BookingDto update(int bookingId, int userId, Boolean approved);

    BookingDto getBookingById(int bookingId, int userId);

    List<BookingDto> getBookingsPageable(String state, int userId, int from, int size);

    List<BookingDto> getBookingsOwner(String state, int userId, Integer from, Integer size);

    ShortBookingInfo getLastBooking(int itemId);

    ShortBookingInfo getNextBooking(int itemId);

}
