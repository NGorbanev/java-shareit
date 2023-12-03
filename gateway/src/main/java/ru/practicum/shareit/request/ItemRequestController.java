package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID = "X-Sharer-User-id";

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader(USER_ID) long requesterId) {
        log.info("POST request for creating ItemRequest by userId={} received", requesterId);
        return itemRequestClient.create(requesterId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable("requestId") long itemRequestId,
                                                     @RequestHeader(USER_ID) long userId) {
        log.info("GET /requestId request received. RequestId={}", itemRequestId);
        return itemRequestClient.getItemRequestById(itemRequestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(USER_ID) long userId) {
        log.info("GET own requests received for getting all own ItemRequests for user id={}", userId);
        return itemRequestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID) long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET /all request for getting all ItemRequests received from userId={}. Paging from={}, size={}",
                userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }
}
