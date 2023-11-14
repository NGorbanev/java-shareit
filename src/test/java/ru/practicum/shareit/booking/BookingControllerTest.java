package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static final String USER_ID = "X-Sharer-User-Id";

    private IncomingBookingDto incomingBookingDto = IncomingBookingDto.builder()
            .itemId(1)
            .start(LocalDateTime.of(2024, 10, 9, 8, 7))
            .end(LocalDateTime.of(2025, 10, 9, 8, 7))
            .build();

    private ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("Item name")
            .description("Item Description")
            .available(true)
            .build();

    private UserDto userDto = UserDto.builder()
            .id(1)
            .name("uName")
            .email("e@mail.com")
            .build();

    private BookingDto bookingDto = BookingDto.builder()
            .id(1)
            .start(LocalDateTime.of(2024, 10, 9, 8, 7))
            .end(LocalDateTime.of(2025, 10, 9, 8, 7))
            .item(itemDto)
            .booker(userDto)
            .status(BookingStatus.WAITING)
            .build();
    private List<BookingDto> bookingDtoList = new ArrayList<>();

    @Test
    public void createBooking() throws Exception {
        when(bookingService.create(any(), any(int.class))).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), int.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void getBooking() throws Exception {
        when(bookingService.getBookingById(any(int.class), any(int.class))).thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), int.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void UnknownStateExceptionResponseTest() throws Exception {
        when(bookingService.getBookingById(any(int.class), any(int.class)))
                .thenThrow(new UnknownStateException("SOMESTATE"));
        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Unknown state: SOMESTATE", result.getResolvedException().getMessage()));

    }

    @Test
    public void getBookings() throws Exception {
        when(bookingService.getBookingsPageable(
                any(String.class),
                any(int.class),
                any(Integer.class),
                nullable(Integer.class))).thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), int.class))
                .andExpect(jsonPath("$.[0].start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void getBookingsByOwner() throws Exception {
        when(bookingService.getBookingsOwner(
                any(String.class),
                any(int.class),
                any(Integer.class),
                nullable(Integer.class))).thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner?from=0&size=10")
                        .content(mapper.writeValueAsString(bookingDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), int.class))
                .andExpect(jsonPath("$.[0].start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    public void updateBooking() throws Exception {
        when(bookingService.update(any(int.class), any(int.class), any(Boolean.class))).thenReturn(bookingDto);
        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), int.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }
}
