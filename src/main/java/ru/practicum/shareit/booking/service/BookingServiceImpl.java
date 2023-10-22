package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.utils.ItemsValidator;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utils.UserValidator;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService{

    private final UserService userService;
    private final ItemService itemService;
    private final ItemsValidator itemsValidator;
    private final UserValidator userValidator;

    @Autowired
    public BookingServiceImpl(UserService userService,
                              ItemService itemService,
                              ItemsValidator itemsValidator,
                              UserValidator userValidator) {
        this.itemService = itemService;
        this.userService = userService;
        this.itemsValidator = itemsValidator;
        this.userValidator = userValidator;
    }

    @Override
    public BookingDto create(IncomingBookingDto bookingDto, int bookerId) {
        return null;
    }

    @Override
    public BookingDto update(int bookingId, int userId, Boolean approved) {
        return null;
    }

    @Override
    public BookingDto getBookingById(int bookingId, int userId) {
        return null;
    }

    @Override
    public List<BookingDto> getBookings(String state, int userId) {
        return null;
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, int userId) {
        return null;
    }

    @Override
    public ShortBookingInfo getLastBooking(int itemId) {
        return null;
    }

    @Override
    public ShortBookingInfo getNextBooking(int itemId) {
        return null;
    }

    @Override
    public Booking getBookingWithUserBookedItem(int itemId, int userId) {
        return null;
    }
}
