package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Collection<ItemDto> getAllItemsOfUser(int userId, int page, int size);

    ItemDto create(ItemDto itemDto, Integer userId);

    ItemDto get(int itemId, int userId);
    public Collection<ItemDto> getAllItemsOfUserPageable(int userId, PageRequest pageRequest);

    ItemDto update(int itemId, ItemDto itemDto, int userId);

    boolean delete(int itemId, int userId);

    Collection<ItemDto> search(String text, int page, int size);

    CommentDto addComment(CommentDto commentDto, int itemId, int userId);

    List<CommentDto> getCommentsByItemId(int itemId);

    List<ItemDto> getItemsByRequestId(int requestId);
}
