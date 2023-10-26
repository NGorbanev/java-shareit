package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.item.utils.ItemsValidator;
import ru.practicum.shareit.user.utils.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository br;
    private final ItemsValidator itemsValidator;
    private final UserValidator userValidator;
    private final BookingMapper mapper;

    @Autowired
    public BookingServiceImpl(ItemsValidator itemsValidator,
                              UserValidator userValidator,
                              BookingMapper mapper,
                              BookingRepository br) {
        this.itemsValidator = itemsValidator;
        this.userValidator = userValidator;
        this.mapper = mapper;
        this.br = br;
    }

    @Override
    public BookingDto create(IncomingBookingDto incomingBookingDto, int bookerId) {
        if (!userValidator.validateUserById(bookerId)) {
            throw new NotFoundException(String.format("User id=%s not found", bookerId));
        }
        if (!itemsValidator.isAvailable(incomingBookingDto.getItemId())) {
            throw new ValidatonException(
                    String.format("Item id=%s is not available for booking", incomingBookingDto.getItemId()));
        }

        // Next check (for dates) was added only for passing Postman tests.
        // Because this check is already performed at Booking class and at database.
        if (incomingBookingDto.getStart().isBefore(LocalDateTime.now()) || (
                incomingBookingDto.getEnd().equals(incomingBookingDto.getStart()) ||
                        incomingBookingDto.getEnd().isBefore(incomingBookingDto.getStart())
        )) {
            throw new ValidatonException("Wrong start or end date");
        }
        Booking newBooking = mapper.toBooking(incomingBookingDto, bookerId);
        if (itemsValidator.ownerMatch(newBooking.getItem().getId(), bookerId)) {
            throw new NotFoundException("Booking can't be made by item's owner");
        }

        try {
            BookingDto result = mapper.toBookingDto(br.save(newBooking));
            return result;
        } catch (DataIntegrityViolationException e) {
            throw new ValidatonException(
                    String.format("Wrong booking dates: Start: %s, end: %s",
                            incomingBookingDto.getStart(),
                            incomingBookingDto.getEnd()));
        }
    }

    @Override
    public BookingDto update(int bookingId, int userId, Boolean approved) {
        if (!userValidator.validateUserById(userId)) {
            throw new NotFoundException(String.format("User id=%s not found", userId));
        }
        Booking booking = br.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Booking id=%s was not found", bookingId)
        ));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidatonException("Booking time has expired");
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (itemsValidator.ownerMatch(booking.getItem().getId(), userId)) {
                if (approved) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
                return mapper.toBookingDto(br.save(booking));
            } else if (booking.getBooker().getId() == userId) {
                if (!approved) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    return mapper.toBookingDto(br.save(booking));
                } else {
                    throw new NotFoundException("Only item's owner can approve the booking");
                }
            } else {
                throw new NotFoundException("Operation on allowed for userId=" + userId);
            }
        } else {
            throw new ValidatonException(String.format("No way to change status %s", booking.getStatus()));
        }
    }

    @Override
    public BookingDto getBookingById(int bookingId, int userId) {
        if (!userValidator.validateUserById(userId)) {
            throw new NotFoundException(String.format("User id=%s not found", userId));
        }
        Booking booking = br.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Booking id=%s was not found", bookingId)));
        if (itemsValidator.ownerMatch(booking.getItem().getId(), userId) ||
                booking.getBooker().getId() == userId) {
            return mapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Only item owner or booking requester can view booking information");
        }
    }

    @Override
    public List<BookingDto> getBookings(String state, int userId) {
        if (!userValidator.validateUserById(userId)) {
            throw new NotFoundException(String.format("User id=%s not found", userId));
        }
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = br.findByBookerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = br.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = br.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = br.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "WAITING":
                bookings = br.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = br.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            default:
                throw new UnknownStateException(state);
        }
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOwner(String state, int userId) {
        if (!userValidator.validateUserById(userId)) {
            throw new NotFoundException(String.format("User id=%s not found", userId));
        }
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = br.findByItem_Owner_Id(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = br.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = br.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = br.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "WAITING":
                bookings = br.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = br.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            default:
                throw new UnknownStateException(state);
        }
        return bookings.stream()
                .map(mapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShortBookingInfo getLastBooking(int itemId) {
        Booking result = br.findFirstByItem_IdAndStartBeforeOrderByEndDesc(itemId, LocalDateTime.now());
        return mapper.toShortBookingInfo(result);
    }

    @Override
    public ShortBookingInfo getNextBooking(int itemId) {
        Booking result = br.findNextBooking(itemId);
        return mapper.toShortBookingInfo(result);
    }

    @Override
    public Booking getBookingWithUserBookedItem(int itemId, int userId) {
        Booking result = br.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(
                itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);
        return result;
    }
}
