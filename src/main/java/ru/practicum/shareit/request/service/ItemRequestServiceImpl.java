package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.utils.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
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
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(int itemRequestId, int userId) {
        log.info("Get item request by id received");
        UserDto userDto = userService.getUser(userId);
        ItemRequest itemRequest = repository.findById(itemRequestId).orElseThrow(
                () -> new ItemRequestNotFound(String.format("Item request id=%s not found", itemRequestId)));
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOwnItemRequests(int requesterId) {
        log.info("Get Own Item Requests received");
        UserDto userDto = userService.getUser(requesterId);
        return repository.findAllByRequesterId(requesterId, Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequests(int userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<ItemRequestDto> requests = repository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageRequest)
                .stream().map(itemRequestMapper::toItemRequestDto).collect(toList());
        return requests;
    }
}
