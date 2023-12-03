package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

//import javax.validation.Validation;
//import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class IncomingBookingDtoTest {
    private JacksonTester<IncomingBookingDto> json;
    private IncomingBookingDto incomingBookingDto;

    public IncomingBookingDtoTest(@Autowired JacksonTester<IncomingBookingDto> json) {
        this.json = json;
        //ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    }

    @BeforeEach
    public void bedoreEach() {
        incomingBookingDto = IncomingBookingDto.builder()
                .itemId(1)
                .start(LocalDateTime.of(2023,12,11,10,9))
                .end(LocalDateTime.of(2024,12,11,10,9))
                .build();
    }

    @Test
    public void testJsonBookingInputDto() throws Exception {
        JsonContent<IncomingBookingDto> result = json.write(incomingBookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-12-11T10:09:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-12-11T10:09:00");
    }
}
