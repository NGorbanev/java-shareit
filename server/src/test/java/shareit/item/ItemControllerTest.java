package shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private static final String USER_ID = "X-Sharer-User-Id";
    private User user = User.builder()
            .id(1)
            .name("Ivan")
            .email("ivan@muromets.rus")
            .build();
    private Item item = Item.builder()
            .id(1)
            .name("Меч кладинец")
            .description("Богатырский")
            .available(true)
            .owner(user)
            .build();
    private ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("Меч кладинец")
            .description("Богатырский")
            .available(true)
            .build();
    private CommentDto commentDto = CommentDto.builder()
            .id(1)
            .item(item)
            .text("Some Comment")
            .created(LocalDateTime.of(2023, 11, 12, 15, 45))
            .authorName(user.getName())
            .build();
    private List<ItemDto> itemsDtoList = new ArrayList<>();

    @Test
    public void createItem() throws Exception {
        when(itemService.create(any(), any(int.class))).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), int.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    public void getItem() throws Exception {
        when(itemService.get(any(int.class), any(int.class))).thenReturn(itemDto);
        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), int.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    public void getItemsOfOwner() throws Exception {
        when(itemService.getAllItemsOfUser(any(int.class), any(int.class), any(int.class))).thenReturn(List.of(itemDto));
        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemsDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), int.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }

    @Test
    public void deleteItemTest() throws Exception {
        mvc.perform(delete("/items/1").header(USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    public void searchItemsTest() throws Exception {
        when(itemService.search(any(String.class), any(int.class), any(int.class))).thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text=Богатырский")
                        .content(mapper.writeValueAsString(itemsDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), int.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }

    @Test
    public void commentCreationTest() throws Exception {
        when(itemService.addComment(any(), any(int.class), any(int.class))).thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), int.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void validationExceptionResponseTest() throws Exception {
        when(itemService.addComment(any(), any(int.class), any(int.class)))
                .thenThrow(new ValidatonException("User id=1 has never booked item id=1"));
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemTest() throws Exception {
        when(itemService.update(any(int.class), any(), any(int.class))).thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), int.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    // ExceptionsHandlerTests (for maximise coverage)
    @Test
    public void notFoundExceptionTest() throws Exception {
        when(itemService.get(any(int.class), any(int.class))).thenThrow(new NotFoundException(String.format("Item id=%s not found", 1)));
        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isNotFound())
                .andExpect(mvc -> mvc.getResolvedException().getClass().equals(NotFoundException.class))
                .andExpect(result -> assertEquals(
                        "Object not found: Item id=1 not found", result.getResolvedException().getMessage()));
    }

    @Test
    public void npeExceptionTest() throws Exception {
        when(itemService.get(any(int.class), any(int.class))).thenThrow(new NullPointerException("Some reason"));
        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(mvc -> mvc.getResolvedException().getClass().equals(NotFoundException.class))
                .andExpect(result -> assertEquals(
                        "Some reason", result.getResolvedException().getMessage()));
    }
}
