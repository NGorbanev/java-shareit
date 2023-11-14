package ru.practicum.shareit.utils;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemsValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.utils.UserValidator;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemsValidatorTest {
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserValidator userValidator;

    private ItemsValidator itemsValidator;
    private MockMvc mvc;

    private User user = User.builder()
            .id(1)
            .build();
    private Item item = Item.builder()
            .owner(user)
            .build();

    @BeforeEach
    public void beforeEach() {
        itemsValidator = new ItemsValidator(itemRepository, userRepository, userValidator);
    }

    @Test
    public void shouldReachNpe() {
        Assertions.assertThrows(NullPointerException.class,
                () -> itemsValidator.ownerMatch(null, null));
    }

    @Test
    public void shouldDropExceptionWhenOwnerCheckIfUserWasNotFound() {
        when(userRepository.findById(any(int.class))).thenReturn(Optional.empty());
        when(itemRepository.findById(any(int.class))).thenReturn(Optional.of(item));
        when(userValidator.validateUserById(any(int.class))).thenReturn(true);
        Assertions.assertThrows(NotFoundException.class,
                () -> itemsValidator.ownerMatch(1, 2));
    }

    @Test
    public void shouldThrowExceptionWhenOwnerCheckIfItemNotFound() {
        when(userRepository.findById(any(int.class))).thenReturn(Optional.of(user));
        when(itemRepository.findById(any(int.class))).thenReturn(Optional.empty());
        when(userValidator.validateUserById(any(int.class))).thenReturn(true);
        Assertions.assertThrows(NotFoundException.class,
                () -> itemsValidator.ownerMatch(1, 2));
    }

    @Test
    public void shouldThrowExceptionWhenIsAvailableCheckIfItemIdIsNullOrZero() {
        Assertions.assertThrows(NullPointerException.class,
                () -> itemsValidator.isAvailable(null));
        Assertions.assertThrows(NullPointerException.class,
                () -> itemsValidator.isAvailable(0));
    }

    @Test
    public void shouldThrowExceptionWhenIsAvailableCheckIfItemWasNotFound() {
        when(itemRepository.findById(any(int.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> itemsValidator.isAvailable(1));
    }
}
