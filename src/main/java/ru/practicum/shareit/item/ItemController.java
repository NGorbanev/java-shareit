package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
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
    public Item postItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(USER_ID) int userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item patchItem(@PathVariable int itemId, @RequestBody Item item, @RequestHeader(USER_ID) int userId) {
        return itemService.update(itemId, item, userId);
    }

    @DeleteMapping("/{itemId}")
    public boolean deleteItem(@PathVariable int itemId, @RequestHeader(USER_ID) int userId) {
        return itemService.delete(itemId, userId);
    }

    @GetMapping()
    public Collection<Item> getAllItemsOfUser(@RequestHeader(USER_ID) int userId) {
        return itemService.getAllItemsOfUser(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable int itemId) {
        return itemService.get(itemId);
    }

    @GetMapping("/search")
    public Collection<Item> serchForItem(@NotNull @RequestParam(name = "text") String searchQuery) {
        return itemService.search(searchQuery);
    }
}
