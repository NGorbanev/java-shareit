package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;


@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto create(@RequestBody IncomingBookingDto incomingBookingDto,
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
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(required = false, defaultValue = "10") int size) {

        log.debug("GetBookings request received");
        log.debug("Parameters: state={}, userId={}, from={}, size={}", state, userId, from, size);
        return service.getBookingsPageable(state, userId, from, size);
    }


    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                                   @RequestHeader(USER_ID) int userId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return service.getBookingsOwner(state, userId, from, size);
    }
}
