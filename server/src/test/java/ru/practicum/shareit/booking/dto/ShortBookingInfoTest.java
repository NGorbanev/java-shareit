package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ShortBookingInfo;

//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ShortBookingInfoTest {
    private JacksonTester<ShortBookingInfo> json;
    private ShortBookingInfo shortBookingInfo;
    //private Validator validator;

    @Autowired
    public ShortBookingInfoTest(JacksonTester<ShortBookingInfo> json) {
        this.json = json;
        //ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        //this.validator = factory.getValidator();
    }

    @BeforeEach
    public void beforeEach() {
        shortBookingInfo = ShortBookingInfo.builder()
                .id(1)
                .bookerId(2)
                .start(LocalDateTime.of(2023,11,10,9,8)) //"2023-11-10T09:08:00"
                .end(LocalDateTime.of(2024,11,10,9,8)) //"2024-11-10T09:08:00"
                .build();
    }

    @Test
    public void testJsonShortBookingInfo() throws Exception {
        JsonContent<ShortBookingInfo> result = json.write(shortBookingInfo);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-11-10T09:08:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-11-10T09:08:00");

    }
}
