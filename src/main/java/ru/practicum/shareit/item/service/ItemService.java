package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<ItemDto> getAllItems();

    Collection<ItemDto> getAllItemsOfUser(int userId);

    ItemDto create(ItemDto itemDto, Integer userId);

    ItemDto get(int itemId, int userId);

    ItemDto update(int itemId, ItemDto itemDto, int userId);

    boolean delete(int itemId, int userId);

    Collection<ItemDto> search(String text);

    CommentDto addComment(CommentDto commentDto, int itemId, int userId);

    List<CommentDto> getCommentsByItemId(int itemId);
}
