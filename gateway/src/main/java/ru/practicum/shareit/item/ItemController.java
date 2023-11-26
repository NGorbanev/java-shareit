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
import java.util.Collection;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemService;
    private static final String USER_ID = "X-Sharer-User-id";

    @PostMapping()
    public ResponseEntity<Object> postItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(USER_ID) long userId) {
        log.debug(String.format("POST request received. UserId=%s Object=%s", userId, itemDto));
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@PathVariable int itemId, @RequestBody ItemDto itemDto, @RequestHeader(USER_ID) long userId) {
        log.debug(String.format("PATCH request received. UserId=%s Object=%s", userId, itemDto));
        return itemService.update(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable int itemId, @RequestHeader(USER_ID) long userId) {
        log.debug(String.format("DELETE request received. UserId=%s , itemId=%s", userId, itemId));
        return itemService.delete(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItemsOfUser(
            @RequestHeader(USER_ID) long userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.debug(String.format("GET request received. getAllItemsOfUser method used"));
        return itemService.getAllItemsOfUser(userId, page, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable int itemId, @RequestHeader(USER_ID) long userId) {
        log.debug("GET itemId={}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object>  searchForItem(
            @NotNull @RequestParam(name = "text") String searchQuery,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestHeader(USER_ID) long userId) {
        log.debug(String.format("GET request received. Searching for '%s'", searchQuery));
        return itemService.search(userId, searchQuery, page, size);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@Valid @RequestBody CommentDto commentDto,
                                  @RequestHeader(USER_ID) long userId, @PathVariable long itemId) {
        log.info("POST request received. Posting comment by userId={} to itemId={}", userId, itemId);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
