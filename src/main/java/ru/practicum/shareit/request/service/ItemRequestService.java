package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, int requesterId, LocalDateTime created);
    ItemRequestDto getItemRequestById(int itemRequestId, int userId);
    List<ItemRequestDto> getOwnItemRequests(int requesterId);
    List<ItemRequestDto> getAllItemRequests(int userId, Integer from, Integer size);
}
