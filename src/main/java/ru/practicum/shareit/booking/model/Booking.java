package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Booking {
    Long id;
    LocalDate start;
    LocalDate end;
    Long itemId;
    Long bookerId;
    BookingStatus status;
}