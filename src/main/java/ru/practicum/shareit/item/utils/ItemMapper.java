package ru.practicum.shareit.item.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

@Component
public class ItemMapper {

    private final BookingService bookingService;
    private final ItemService itemService;

    @Autowired
    @Lazy
    public ItemMapper(BookingService bs, ItemService is) {
        this.bookingService = bs;
        this.itemService = is;
    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .comments(itemService.getCommentsByItemId(item.getId()))
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
                .build();
    }

    public ItemDto itemDtoExtended(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .lastBooking(bookingService.getLastBooking(item.getId()))
                .nextBooking(bookingService.getNextBooking(item.getId()))
                .comments(itemService.getCommentsByItemId(item.getId()))
                .build();
    }
}
