package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
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
        log.info("POST request for creating ItemRequest by userId={} received", requesterId);
        return service.create(itemRequestDto, requesterId, LocalDateTime.now());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable("requestId") int itemRequestId,
                                             @RequestHeader(USER_ID) int userId) {
        log.info("GET request received for getting ItemRequest with id={}", itemRequestId);
        return service.getItemRequestById(itemRequestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(USER_ID) int userId) {
        log.info("GET request received for geting all own ItemRequests for user id={}", userId);
        return service.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID) int userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(required = false) Integer size) {
        log.info("GET request for getting all ItemRequests received from userId={}", userId);
        return service.getAllItemRequests(userId, from, size);
    }
}
