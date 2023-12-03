package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

/*import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;*/

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    private JacksonTester<UserDto> json;
    private UserDto userDto;
    //private Validator validator;

    @Autowired
    public UserDtoTest(JacksonTester<UserDto> json) {
        this.json = json;
        //ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        //this.validator = factory.getValidator();
    }

    @BeforeEach
    public void preparation() {
        userDto = UserDto.builder()
                .id(1)
                .name("Petya")
                .email("petya@sobaka.ru")
                .build();
    }

    @Test
    public void jsonUserDtoTest() throws Exception {
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Petya");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("petya@sobaka.ru");
    }
/*
    @Test
    public void ifUSerDtoIsValidViolationsAreEmpty() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations.isEmpty());
    }

    @Test
    public void ifUserDtoNameIsBlankViolationsShouldHaveNotBlankMessage() {
        userDto.setName(" ");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(!violations.isEmpty());
        assertThat(violations.toString().contains("must not be blank"));
    }

    @Test
    public void ifUserDtoNameIsNullViolationsMessageNotBlamkShoildBeReceived() {
        userDto.setName(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(!violations.isEmpty());
        assertThat(violations.toString().contains("must not be blank"));
    }

    @Test
    public void ifUserDtoEmailIsEmptyNotBlankViolationShouldBeReceived() {
        userDto.setEmail(" ");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(!violations.isEmpty());
        assertThat(violations.toString().contains("must not be blank"));
    }

    @Test
    public void ifUserDtoEmailIsNullNotBlankViolationShouldBeReceived() {
        userDto.setEmail(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(!violations.isEmpty());
        assertThat(violations.toString().contains("must not be blank"));
    }

    @Test
    public void ifUserDtoEmailIsNotFormattedThanFormatViolationShouldBeReceived() {
        userDto.setEmail("someEmail");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(!violations.isEmpty());
        assertThat(violations.toString().contains("must be a well-formed email address"));
    }

 */
}
