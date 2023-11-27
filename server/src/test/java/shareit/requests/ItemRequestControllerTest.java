package shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService service;
    @Autowired
    private MockMvc mvc;
    private static final String USER_ID = "X-Sharer-User-Id";
    private UserDto userDto = UserDto.builder()
            .name("Petya")
            .email("petya@rambler.wtf")
            .build();

    private ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("Item1")
            .description("Some item description")
            .available(true)
            .requestId(1)
            .build();

    private ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1)
            .description("Description")
            .requester(userDto)
            .created(LocalDateTime.now())
            .items(List.of(itemDto))
            .build();

    @Test
    public void createItemRequest() throws Exception {
        when(service.create(any(), any(int.class), any(LocalDateTime.class))).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), int.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId())))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void getItemRequest() throws Exception {
        when(service.getItemRequestById(any(int.class), any(int.class))).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), int.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), int.class))
                .andExpect(jsonPath("$.requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void getOwnItemRequests() throws Exception {
        when(service.getOwnItemRequests(any(int.class))).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), int.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(itemRequestDto.getRequester().getId()), int.class))
                .andExpect(jsonPath("$.[0].requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    public void getAllItemRequests() throws Exception {
        when(service.getAllItemRequests(any(int.class), any(), any())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), int.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(itemRequestDto.getRequester().getId()), int.class))
                .andExpect(jsonPath("$.[0].requester.name", is(itemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(itemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
