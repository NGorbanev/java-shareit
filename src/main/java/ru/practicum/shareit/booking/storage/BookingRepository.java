package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Page<Booking> findByBookerId(int bookerId, PageRequest pageRequest);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(
            int userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(int userId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(int userId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(int userId, BookingStatus status, Pageable pageable);

    Page<Booking> findByItem_Owner_Id(int userId, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            int userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStartIsAfter(int userId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndEndIsBefore(int userId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStatus(int userId, BookingStatus status, Pageable pageable);

    Booking findFirstByItem_IdAndStartBeforeOrderByEndDesc(int itemId, LocalDateTime end);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(
            int itemId, int userId, LocalDateTime end, BookingStatus status);

    @Query(value = "SELECT b.*" +
            "FROM BOOKINGS b " +
            "JOIN ITEMS i ON b.ITEM_ID = i.ID " +
            "JOIN USERS u ON i.OWNER_ID = u.ID " +
            "WHERE i.ID = ? AND START_DATE > CURRENT_TIMESTAMP AND STATUS != 'REJECTED'" +
            "ORDER BY START_DATE ASC " +
            "LIMIT 1", nativeQuery = true)
    Booking findNextBooking(int itemId);


}
