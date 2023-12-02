package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class IncomingBookingDtoTest {
    private JacksonTester<IncomingBookingDto> json;
    private IncomingBookingDto incomingBookingDto;
    private Validator validator;

    public IncomingBookingDtoTest(@Autowired JacksonTester<IncomingBookingDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
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

    @Test
    public void startDateIsFutureOrPresentViolationTest() {
        incomingBookingDto.setStart(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<IncomingBookingDto>> violations = validator.validate(incomingBookingDto);
        System.out.println(violations);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString())
                .contains("messageTemplate='{javax.validation.constraints.FutureOrPresent.message}'");
    }

    @Test
    public void endDateIsInFutureTest() {
        incomingBookingDto.setEnd(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<IncomingBookingDto>> violations = validator.validate(incomingBookingDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString())
                .contains("messageTemplate='{javax.validation.constraints.Future.message}'");

    }
}
