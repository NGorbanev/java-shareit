package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.ShortBookingInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerId(int bookerId, Sort sort);
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter (
            int userId, LocalDateTime start, LocalDateTime end, Sort sort);
    List<Booking> findByBookerIdAndEndIsBefore(int userId, LocalDateTime end, Sort sort);
    List<Booking> findByBookerIdAndStartIsAfter(int userId, LocalDateTime start, Sort sort);
    List<Booking> findByBookerIdAndStatus(int userId, BookingStatus status, Sort sort);
    List<Booking> findByItem_Owner_Id(int userId, Sort sort);
    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            int userId, LocalDateTime start, LocalDateTime end, Sort sort);
    List<Booking> findByItem_Owner_IdAndStartIsAfter (int userId, LocalDateTime start, Sort sort);
    List<Booking> findByItem_Owner_IdAndEndIsBefore(int userId, LocalDateTime end, Sort sort);
    List<Booking> findByItem_Owner_IdAndStatus(int userId, BookingStatus status, Sort sort);
    Booking findFirstByItem_IdAndEndBeforeOrderByEndDesc(int itemId, LocalDateTime end);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(
            int itemId, int userId, LocalDateTime end, BookingStatus status);

    @Query(value = "SELECT b.*" +
            "FROM BOOKINGS b " +
            "JOIN ITEMS i ON b.ITEM_ID = i.ID " +
            "JOIN USERS u ON i.OWNER_ID = u.ID " +
            "WHERE i.ID = ? AND START_DATE > CURRENT_TIMESTAMP " +
            "ORDER BY START_DATE ASC " +
            "LIMIT 1", nativeQuery = true)
    Booking findNextBooking(int itemId);


}
