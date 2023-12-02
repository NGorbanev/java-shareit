package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.UnknownStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookItemRequestDto incomingBookingDto,
                                         @RequestHeader(USER_ID) @Positive long bookerId) {
        log.info("POST Booking create request userId={}, payload:{}", bookerId, incomingBookingDto);
        return bookingClient.addBooking(bookerId, incomingBookingDto);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) @Positive long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnknownStateException(stateParam));
        log.info("GET bookings with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) @Positive long userId,
                                             @PathVariable @Positive Long bookingId) {
        log.info("GET /bookingId {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                                   @RequestHeader(USER_ID) @Positive long userId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /owner received. State={}, userId={}, from={}, size={}", state, userId, from, size);
        BookingState checkState = BookingState.from(state).orElseThrow(
                () -> new UnknownStateException(state));
        return bookingClient.getBookingForCurrentOwner(userId, checkState, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable @Positive int bookingId,
                             @RequestHeader(USER_ID) @Positive long userId,
                             @RequestParam Boolean approved) {
        log.info("PATCH /booking received. BookingId={}, userId={}, approve={}", bookingId, userId, approved);
        return bookingClient.approveStatus(userId, bookingId, approved);
    }
}