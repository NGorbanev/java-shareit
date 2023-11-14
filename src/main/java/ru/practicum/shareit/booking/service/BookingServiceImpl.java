package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.utils.Pagination;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemsValidator itemsValidator;
    private final UserValidator userValidator;
    private final BookingMapper mapper;

    @Autowired
    public BookingServiceImpl(ItemsValidator itemsValidator,
                              UserValidator userValidator,
                              BookingMapper mapper,
                              BookingRepository bookingRepository) {
        this.itemsValidator = itemsValidator;
        this.userValidator = userValidator;
        this.mapper = mapper;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingDto create(IncomingBookingDto incomingBookingDto, int bookerId) {
        log.info("Booking request from userId={} to itemId={}", bookerId, incomingBookingDto.getItemId());
        userValidator.validateUserById(bookerId);
        if (!itemsValidator.isAvailable(incomingBookingDto.getItemId())) {
            log.warn("Item is not available for booking");
            throw new ValidatonException(
                    String.format("Item id=%s is not available for booking", incomingBookingDto.getItemId()));
        }
        if (incomingBookingDto.getStart().isBefore(LocalDateTime.now()) || (
                incomingBookingDto.getEnd().equals(incomingBookingDto.getStart()) ||
                        incomingBookingDto.getEnd().isBefore(incomingBookingDto.getStart())
        )) {
            log.warn("Wrong start or end date");
            throw new ValidatonException("Wrong start or end date");
        }
        Booking newBooking = mapper.toBooking(incomingBookingDto, bookerId);
        if (itemsValidator.ownerMatch(newBooking.getItem().getId(), bookerId)) {
            log.warn("User id={} can't perform a booking of an owned item id = {}",
                    incomingBookingDto.getItemId(), bookerId);
            throw new NotFoundException("Booking can't be made by item's owner");
        }
        BookingDto result = mapper.toBookingDto(bookingRepository.save(newBooking));
        log.info("Creation of booking id={} performed", result.getId());
        return result;
    }

    @Override
    public BookingDto update(int bookingId, int userId, Boolean approved) {
        log.info("UPDATE method for bookingId={}, from userId={}, approved={}", bookingId, userId, approved);
        userValidator.validateUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Booking id=%s was not found", bookingId)
        ));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            log.warn("Booking time has expired");
            throw new ValidatonException("Booking time has expired");
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (itemsValidator.ownerMatch(booking.getItem().getId(), userId)) {
                if (approved) {
                    log.info("Booking id={} has been approved by owner id={}", bookingId, userId);
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    log.info("Booking id={} has been rejected by owner id={}", bookingId, userId);
                    booking.setStatus(BookingStatus.REJECTED);
                }
                return mapper.toBookingDto(bookingRepository.save(booking));
            } else if (booking.getBooker().getId() == userId) {
                if (!approved) {
                    log.info("Booking id={} has been cancelled by booker id={}", bookingId, userId);
                    booking.setStatus(BookingStatus.CANCELLED);
                    return mapper.toBookingDto(bookingRepository.save(booking));
                } else {
                    log.warn("Access violation for booking id={} for user id={}", bookingId, userId);
                    throw new NotFoundException("Only item's owner can approve the booking");
                }
            } else {
                log.warn("Access violation for booking id={} for user id={}", bookingId, userId);
                throw new NotFoundException("Operation on allowed for userId=" + userId);
            }
        } else {
            log.warn("Couldn't change status {}", booking.getStatus());
            throw new ValidatonException(String.format("No way to change status %s", booking.getStatus()));
        }
    }

    @Override
    public BookingDto getBookingById(int bookingId, int userId) {
        log.info("getBookingById request received");
        userValidator.validateUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Booking id=%s was not found", bookingId)));
        if (itemsValidator.ownerMatch(booking.getItem().getId(), userId) ||
                booking.getBooker().getId() == userId) {
            log.info("getBookingById request is served");
            return mapper.toBookingDto(booking);
        } else {
            log.warn("UserId={} can't get booking information for bookingId={}", userId, booking);
            throw new NotFoundException("Only item owner or booking requester can view booking information");
        }
    }

    @Override
    public List<BookingDto> getBookingsPageable(String state, int userId, Integer from, Integer size) {
        log.info("getBookings with pagination request received");
        userValidator.validateUserById(userId);
        List<BookingDto> result = new ArrayList<>();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Page<Booking> page;
        Pagination pages = new Pagination(from, size);
        if (size == null) {
            pageable = PageRequest.of(pages.getIndex(), pages.getPageSize(), sort);
            do {
                page = getPageForBookings(state, userId, pageable);
                result.addAll(page.stream().map(mapper::toBookingDto).collect(toList()));
                pageable = pageable.next();
            } while (page.hasNext());
        } else {
            for (int i = pages.getIndex(); i < pages.getTotalPages(); i++) {
                pageable = PageRequest.of(i, pages.getPageSize(), sort);
                page = getPageForBookings(state, userId, pageable);
                result.addAll(page.stream().map(mapper::toBookingDto).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            result = result.stream().limit(size).collect(toList());
        }
        return result;
    }

    private Page<Booking> getPageForBookings(String state, int userId, Pageable pageable) {
        Page<Booking> page;
        switch (state) {
            case "ALL":
                page = bookingRepository.findByBookerId(userId, pageable);
                break;
            case "CURRENT":
                page = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case "PAST":
                page = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                page = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                page = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                page = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new UnknownStateException(state);
        }
        return page;
    }

    public List<BookingDto> getBookingsOwner(String state, int userId, Integer from, Integer size) {
        log.info("getBookingsOwner request received");
        userValidator.validateUserById(userId);
        List<BookingDto> result = new ArrayList<>();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Page<Booking> page;
        Pagination pages = new Pagination(from, size);
        if (size == null) {
            pageable = PageRequest.of(pages.getIndex(), pages.getPageSize(), sort);
            do {
                page = getPageBookingsOwner(state, userId, pageable);
                result.addAll(page.stream().map(mapper::toBookingDto).collect(toList()));
                pageable = pageable.next();
            } while (page.hasNext());
        } else {
            for (int i = pages.getIndex(); i < pages.getTotalPages(); i++) {
                pageable = PageRequest.of(i, pages.getPageSize(), sort);
                page = getPageBookingsOwner(state, userId, pageable);
                result.addAll(page.stream().map(mapper::toBookingDto).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            result = result.stream().limit(size).collect(toList());
        }
        return result;
    }

    private Page<Booking> getPageBookingsOwner(String state, int userId, Pageable pageable) {
        Page<Booking> page;
        switch (state) {
            case "ALL":
                page = bookingRepository.findByItem_Owner_Id(userId, pageable);
                log.info("State 'ALL' is served");
                break;
            case "CURRENT":
                page = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                log.info("State 'CURRENT' is served");
                break;
            case "PAST":
                page = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                log.info("State 'PAST' is served");
                break;
            case "FUTURE":
                page = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                log.info("State 'FUTURE' is served");
                break;
            case "WAITING":
                page = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, pageable);
                log.info("State 'WAITING' is served");
                break;
            case "REJECTED":
                page = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
                log.info("State 'REJECTED' is served");
                break;
            default:
                throw new UnknownStateException(state);
        }
        return page;
    }

    @Override
    public ShortBookingInfo getLastBooking(int itemId) {
        Booking result = bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(itemId, LocalDateTime.now());
        return mapper.toShortBookingInfo(result);
    }

    @Override
    public ShortBookingInfo getNextBooking(int itemId) {
        Booking result = bookingRepository.findNextBooking(itemId);
        return mapper.toShortBookingInfo(result);
    }
}
