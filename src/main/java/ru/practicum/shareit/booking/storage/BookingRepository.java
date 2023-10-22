package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.awt.print.Book;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
}
