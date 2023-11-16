package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ValidatonException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto create(@Valid @RequestBody IncomingBookingDto incomingBookingDto,
                             @RequestHeader(USER_ID) int bookerId) {
        return service.create(incomingBookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable int bookingId,
                             @RequestHeader(USER_ID) int userId,
                             @RequestParam Boolean approved) {
        return service.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable int bookingId,
                                     @RequestHeader(USER_ID) int userId) {
        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestHeader(USER_ID) int userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        //I got no idea why @PositiveOrZero check doesnot work correctly. So i had to add manual check..
        if (from < 0) {
            throw new ValidatonException("From value is less than zero");
        }
        return service.getBookingsPageable(state, userId, from, size);
    }


    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                                   @RequestHeader(USER_ID) int userId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        return service.getBookingsOwner(state, userId, from, size);
    }
}
