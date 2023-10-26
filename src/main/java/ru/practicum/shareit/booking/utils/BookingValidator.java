package ru.practicum.shareit.booking.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Optional;

@Component
public class BookingValidator {
    private final BookingService bookingService;

    @Autowired
    public BookingValidator(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public boolean checkIfUserBookedItem(int userId, int itemId) {
        boolean result = false;
        Optional<Booking> booking = Optional.ofNullable(bookingService.getBookingWithUserBookedItem(itemId, userId));
        if (booking.isPresent()) {
            result = true;
        }
        return result;
    }
}
