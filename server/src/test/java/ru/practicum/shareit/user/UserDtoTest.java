package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    private JacksonTester<UserDto> json;
    private UserDto userDto;

    @Autowired
    public UserDtoTest(JacksonTester<UserDto> json) {
        this.json = json;
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
}
