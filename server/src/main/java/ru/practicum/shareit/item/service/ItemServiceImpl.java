package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Transactional
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
    public Collection<ItemDto> getAllItemsOfUser(int userId, int page, int size) {
        log.info("getAllItems from userId={} servicing...", userId);
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("getAllItemsOfUserPageable servicing... ");
        Page<Item> items = itemStorage.findByOwnerIdOrderByIdAsc(userId, pageRequest);
        log.info("Request returned {} items", items.getSize());
        List<Item> itemsList = items.getContent();
        ArrayList<ItemDto> result = new ArrayList<>();
        for (Item i : items) {
            result.add(itemMapper.itemDtoExtended(i));
        }
        log.info("getAllItemsOfUser is serviced");
        return result;
    }

    @Override
    public Collection<ItemDto> getAllItemsOfUserPageable(int userId, PageRequest pageRequest) {
        log.info("getAllItemsOfUserPageable servicing... ");
        Page<Item> items = itemStorage.findByOwnerIdOrderByIdAsc(userId, pageRequest);
        log.info("Request returned {} items", items.getSize());
        return items.stream().map(itemMapper::toItemDto).collect(toList());
    }


    @Override
    public ItemDto create(ItemDto itemDto, Integer userId) {
        log.info("Create for itemDto '{}', owerId={}", itemDto.getName(), userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.getUser(userId)));
        validator.validateItem(item);
        log.info("Create method serviced");
        return itemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemDto get(int itemId, int userId) {
        log.info("Get itemId={} from userId={} received", itemId, userId);
        Optional<Item> item = itemStorage.findById(itemId);
        if (item.isPresent()) {
            log.info("Item id={} found", itemId);
            if (validator.ownerMatch(itemId, userId)) {
                log.info("Returning Item id={} extended info", itemId);
                return itemMapper.itemDtoExtended(item.get());
            } else {
                log.info("Returning Item id={} regular info", itemId);
                return itemMapper.toItemDto(item.get());
            }
        } else {
            log.warn("Item id={} not found", itemId);
            throw new NotFoundException(String.format("Item id=%s not found", itemId));
        }
    }

    @Override
    public ItemDto update(int itemId, ItemDto itemTransferName, int user) {
        log.info("Update request for itemId={} by userId={} servicing", itemId, user);
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
            log.info("ItemId={} is updating..", itemId);
            return itemMapper.toItemDto(itemStorage.save(itemForUpdate));
        } else {
            throw new NotFoundException(String.format("Object not found: %s", itemTransferName));
        }
    }

    @Override
    public boolean delete(int itemId, int userId) {
        log.info("Delete itemId={} by userId={} request is servicing", itemId, userId);
        if (!validator.ownerMatch(itemId, userId)) {
            log.warn("UserId={} can't delete itemId={} because user is not the owner if item", userId, itemId);
            throw new NotAllowedException(
                    String.format("User id=%s is not allowed to delete item id=%s", userId, itemId));
        }
        log.info("Deleting itemId={}", itemId);
        itemStorage.deleteById(itemId);
        return true;
    }

    @Override
    public Collection<ItemDto> search(String text, int page, int size) {
        log.info("Search request for string '{}' is servicing..", text);
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("Getting search results");
        List<ItemDto> result = itemStorage.getItemsBySearchQuery(text.toLowerCase(), pageRequest).stream()
                .map(itemMapper::toItemDto).collect(toList());
        log.info("Results found: {}", result.size());
        return result;
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, int itemId, int userId) {
        log.info("AddComment request received. Processing..");
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
        log.info("addComment is worked out");
        return CommentMapper.toCommentDto(commentsRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(int itemId) {
        log.info("getCommentsByItemId request received. Processing..");
        List<CommentDto> commentDtos = new ArrayList<>();
        commentDtos = commentsRepository.findAllByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
        if (commentDtos == null) {
            log.info("No comments found for item={}", itemId);
            return new ArrayList<>();
        }
        log.info("getCommentsByItemId is worked out");
        return commentDtos;
    }

    @Override
    public List<ItemDto> getItemsByRequestId(int requestId) {
        return itemStorage.findAllByRequestId(requestId,
                        Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}
