package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;
    private static final String USER_ID = "X-Sharer-User-id";

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @ResponseBody
    @PostMapping
    public ItemRequestDto create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(USER_ID) int requesterId) {
        log.info("POST request for creating ItemRequest. UserId={}, request={}", requesterId, itemRequestDto);
        return service.create(itemRequestDto, requesterId, LocalDateTime.now());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable("requestId") int itemRequestId,
                                             @RequestHeader(USER_ID) int userId) {
        log.info("GET /requestId received. ItemRequest id={}", itemRequestId);
        return service.getItemRequestById(itemRequestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(USER_ID) int userId) {
        log.info("GET own request received. UserId={}", userId);
        return service.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID) int userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET /all requests received. UserId={}", userId);
        return service.getAllItemRequests(userId, from, size);
    }
}
