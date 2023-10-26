package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotAllowedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatonException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentsRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.item.utils.ItemsValidator;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.utils.UserMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemStorage;
    private final UserServiceImpl userService;
    private final ItemsValidator validator;
    private final ItemMapper itemMapper;
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemStorage,
                           UserServiceImpl userService,
                           ItemsValidator validator,
                           ItemMapper im,
                           CommentsRepository commentsRepository,
                           BookingRepository br) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.validator = validator;
        this.itemMapper = im;
        this.commentsRepository = commentsRepository;
        this.bookingRepository = br;

    }

    @Override
    public Collection<ItemDto> getAllItems() {
        Collection<ItemDto> result = new ArrayList<>();
        for (Item i : itemStorage.findAll()) {
            result.add(itemMapper.toItemDto(i));
        }
        return result;
    }

    @Override
    public Collection<ItemDto> getAllItemsOfUser(int userId) {
        List<Item> items = itemStorage.findAll().stream()
                .filter(itemDto -> itemDto.getOwner().getId() == userId)
                .collect(Collectors.toList());
        ArrayList<ItemDto> result = new ArrayList<>();
        for (Item i : items) {
            result.add(itemMapper.itemDtoExtended(i));
        }
        return result;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.getUser(userId)));
        validator.validateItem(item);
        return itemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto get(int itemId, int userId) {
        Optional<Item> item = itemStorage.findById(itemId);
        if (item.isPresent()) {
            if (validator.ownerMatch(itemId, userId)) {
                return itemMapper.itemDtoExtended(item.get());
            } else {
                return itemMapper.toItemDto(item.get());
            }
        } else {
            throw new NotFoundException(String.format("Item id=%s not found", itemId));
        }
    }

    @Override
    public ItemDto update(int itemId, ItemDto itemTransferName, int user) {
        if (!validator.ownerMatch(itemId, user)) {
            throw new NotAllowedException(itemTransferName, user);
        }
        Optional<Item> item = itemStorage.findById(itemId);
        if (item.isPresent()) {
            Item itemForUpdate = itemMapper.toItem(itemTransferName);
            itemForUpdate.setId(itemId);
            itemForUpdate.setOwner(item.get().getOwner());
            if (itemTransferName.getName() != null) {
                itemForUpdate.setName(itemTransferName.getName());
            } else {
                itemForUpdate.setName(item.get().getName());
            }
            if (itemTransferName.getDescription() != null) {
                itemForUpdate.setDescription(itemTransferName.getDescription());
            } else {
                itemForUpdate.setDescription(item.get().getDescription());
            }
            if (itemTransferName.getAvailable() != null) {
                itemForUpdate.setAvailable(itemTransferName.getAvailable());
            } else {
                itemForUpdate.setAvailable(item.get().getAvailable());
            }
            if (Integer.valueOf(itemTransferName.getRequestId()) != null) {
                itemForUpdate.setRequestId(itemTransferName.getRequestId());
            } else {
                itemForUpdate.setRequestId(item.get().getRequestId());
            }
            return itemMapper.toItemDto(itemStorage.save(itemForUpdate));
        } else {
            throw new NotFoundException(itemTransferName);
        }
    }

    @Override
    public boolean delete(int itemId, int userId) {
        if (!validator.ownerMatch(itemId, userId)) {
            throw new NotAllowedException(
                    String.format("User id=%s is not allowed to delete item id=%s", userId, itemId));
        }
        itemStorage.deleteById(itemId);
        return true;
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return getAllItems().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toUpperCase().contains(text.toUpperCase()) ||
                                item.getDescription().toUpperCase().contains(text.toUpperCase())))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, int itemId, int userId) {
        Booking booking = Optional.ofNullable(
                bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(
                        itemId,
                        userId,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED))
                .orElseThrow(() -> new ValidatonException(
                        String.format("User id=%s has never booked item id=%s", userId, itemId)));
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .author(booking.getBooker())
                .created(LocalDateTime.now())
                .item(booking.getItem())
                .build();
        return CommentMapper.toCommentDto(commentsRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(int itemId) {
        List<CommentDto> commentDtos = new ArrayList<>();
        commentDtos = commentsRepository.findAllByItem_Id(itemId,
                Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (commentDtos == null) {
            return new ArrayList<>();
        }
        return commentDtos;
    }

}
