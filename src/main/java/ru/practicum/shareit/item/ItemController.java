package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.debug(String.format("POST request received. UserId=%s Object=%s", userId, itemDto));
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable int itemId, @RequestBody ItemDto itemDto, @RequestHeader(USER_ID) int userId) {
        log.debug(String.format("PATCH request received. UserId=%s Object=%s", userId, itemDto));
        return itemService.update(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public boolean deleteItem(@PathVariable int itemId, @RequestHeader(USER_ID) int userId) {
        log.debug(String.format("DELETE request received. UserId=%s , itemId=%s", userId, itemId));
        return itemService.delete(itemId, userId);
    }

    @GetMapping()
    public Collection<ItemDto> getAllItemsOfUser(@RequestHeader(USER_ID) int userId) {
        log.debug(String.format("GET request received. getAllItemsOfUser method used"));
        return itemService.getAllItemsOfUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        log.debug(String.format("GET itemId=%s", itemId));
        return itemService.get(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchForItem(@NotNull @RequestParam(name = "text") String searchQuery) {
        log.debug(String.format("GET request received. Searching for '%s'", searchQuery));
        return itemService.search(searchQuery);
    }
}
