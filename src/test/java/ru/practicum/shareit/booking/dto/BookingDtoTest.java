package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    private JacksonTester<BookingDto> json;
    private BookingDto bookingDto;
    private Validator validator;


    public BookingDtoTest(@Autowired JacksonTester<BookingDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void beforeEach() {

        UserDto userDto = UserDto.builder()
                .id(1)
                .name("UserName")
                .email("user@email.com")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.of(2023, 1, 2, 3, 4))
                .end(LocalDateTime.of(2024, 1, 2, 3, 4))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void testJsonBookingDto() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-02T03:04:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-01-02T03:04:00");
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");


    }
}
