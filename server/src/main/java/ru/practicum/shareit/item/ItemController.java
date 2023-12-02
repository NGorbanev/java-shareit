package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;


@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto postItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(USER_ID) int userId) {
        log.debug("POST item received. UserId={} Object={}", userId, itemDto);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable int itemId, @RequestBody ItemDto itemDto, @RequestHeader(USER_ID) int userId) {
        log.debug("PATCH item request received. UserId={}, itemId={}, Object={}", userId, itemId, itemDto);
        return itemService.update(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public boolean deleteItem(@PathVariable int itemId, @RequestHeader(USER_ID) int userId) {
        log.debug("DELETE item received. UserId={} , itemId={}", userId, itemId);
        return itemService.delete(itemId, userId);
    }

    @GetMapping()
    public Collection<ItemDto> getAllItemsOfUser(
            @RequestHeader(USER_ID) int userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.debug("GET item: all received, userId={}, page={}, size={}", userId, page, size);
        return itemService.getAllItemsOfUser(userId, page, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId, @RequestHeader(USER_ID) int userId) {
        log.debug("GET item Id={}", itemId);
        return itemService.get(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchForItem(
            @RequestParam(name = "text") String searchQuery,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET request /search received. Searching for '{}' Page={}, size={}", searchQuery, from, size);
        return itemService.search(searchQuery, from, size);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@Valid @RequestBody CommentDto commentDto,
                                  @RequestHeader(USER_ID) int userId, @PathVariable int itemId) {
        log.info("POST /itemId/comment request received. Posting comment by userId={} to itemId={}, comment={}",
                userId, itemId, commentDto);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
