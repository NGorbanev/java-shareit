package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.utils.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository repository,
                                  ItemRequestMapper itemRequestMapper,
                                  UserService userService) {
        this.repository = repository;
        this.itemRequestMapper = itemRequestMapper;
        this.userService = userService;

    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, int requesterId, LocalDateTime created) {
        log.info("Create request for ItemRequest received");
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, requesterId, created);
        return itemRequestMapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequestById(int itemRequestId, int userId) {
        log.info("Get item request by id received");
        UserDto userDto = Optional.ofNullable(userService.getUser(userId)).orElseThrow(
                () -> new NotFoundException(String.format("User id=%s was not found", userId))
        );
        ItemRequest itemRequest = repository.findById(itemRequestId).orElseThrow(
                () -> new ItemRequestNotFound(String.format("Item request id=%s not found", itemRequestId)));
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(int requesterId) {
        log.info("Get Own Item Requests received");
        UserDto userDto = Optional.ofNullable(userService.getUser(requesterId)).orElseThrow(
                () -> new NotFoundException(String.format("User id=%s was not found", requesterId))
        );
        return repository.findAllByRequesterId(requesterId, Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(int userId, Integer from, Integer size) {
        log.info("Get All item Requests received");
        UserDto userDto = Optional.ofNullable(userService.getUser(userId)).orElseThrow(
                () -> new NotFoundException(String.format("User id=%s was not found", userId))
        );
        List<ItemRequestDto> listItemRequestDto = new ArrayList<>();
        Pageable pageable;
        Page<ItemRequest> page;
        Pagination pager = new Pagination(from, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        if (size == null) {
            List<ItemRequest> listItemRequest = repository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
            listItemRequestDto
                    .addAll(listItemRequest.stream()
                            .skip(from)
                            .map(itemRequestMapper::toItemRequestDto).collect(toList()));
        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                pageable =
                        PageRequest.of(i, pager.getPageSize(), sort);
                page = repository.findAllByRequesterIdNot(userId, pageable);
                listItemRequestDto.addAll(page.stream().map(itemRequestMapper::toItemRequestDto).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listItemRequestDto = listItemRequestDto.stream().limit(size).collect(toList());
        }
        return listItemRequestDto;
    }
}
