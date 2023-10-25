package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingInfo;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingDto create(IncomingBookingDto bookingDto, int bookerId);

    BookingDto update(int bookingId, int userId, Boolean approved);

    BookingDto getBookingById(int bookingId, int userId);

    List<BookingDto> getBookings(String state, int userId);

    List<BookingDto> getBookingsOwner(String state, int userId);

    ShortBookingInfo getLastBooking(int itemId);

    ShortBookingInfo getNextBooking(int itemId);

    BookingDto getBookingWithUserBookedItem(int itemId, int userId);

}
