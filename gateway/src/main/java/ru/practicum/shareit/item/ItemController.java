package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-id";

    @PostMapping()
    public ResponseEntity<Object> postItem(@RequestBody @Valid ItemDto itemDto,
                                           @RequestHeader(USER_ID) long userId) {
        log.debug("POST request received. UserId={} Object={}", userId, itemDto);
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@PathVariable int itemId,
                                            @RequestBody ItemDto itemDto,
                                            @RequestHeader(USER_ID) long userId) {
        log.debug("PATCH /itemId request received. ItemId={} UserId={} Object={}", itemId, userId, itemDto);
        return itemClient.update(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable int itemId,
                                             @RequestHeader(USER_ID) long userId) {
        log.debug("DELETE /itemId request received. UserId={} , itemId={}", userId, itemId);
        return itemClient.delete(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItemsOfUser(
            @RequestHeader(USER_ID) long userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.debug("GET all items request received. getAllItemsOfUser method used. Page={}, size={}", page, size);
        return itemClient.getAllItemsOfUser(userId, page, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable int itemId,
                                              @RequestHeader(USER_ID) long userId) {
        log.debug("GET /itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchForItem(
            @NotNull @RequestParam(name = "text") String searchQuery,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestHeader(USER_ID) long userId) {
        log.debug(String.format("GET /search request received. UserId={}, page={}, size={}. Searching for '%s'",
                userId, page, size, searchQuery));
        return itemClient.search(userId, searchQuery, page, size);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@Valid @RequestBody CommentDto commentDto,
                                              @RequestHeader(USER_ID) long userId, @PathVariable long itemId) {
        log.info("POST /comment request received. Posting comment {} by userId={} to itemId={}", commentDto, userId, itemId);
        return itemClient.addComment(commentDto, itemId, userId);
    }
}
