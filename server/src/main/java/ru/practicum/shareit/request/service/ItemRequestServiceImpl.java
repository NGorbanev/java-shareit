package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.utils.ItemRequestMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository repository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository,
                                  ItemMapper itemMapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;

    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, int requesterId, LocalDateTime created) {
        log.info("Create request for ItemRequest received");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(
                itemRequestDto,
                userRepository.findById(requesterId).orElseThrow(
                        () -> new NotFoundException(String.format("User id=%s not found", requesterId))),
                created);
        ItemRequest savedItemRequest = repository.save(itemRequest);
        List<ItemDto> itemDtosOfRequest = itemRepository.findAllByRequestId(savedItemRequest.getId(), Sort.unsorted())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
        return ItemRequestMapper.toItemRequestDto(savedItemRequest, itemDtosOfRequest);
    }


    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(int itemRequestId, int userId) {
        log.info("Get item request by id received");
        Optional.of(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User id=%s not found", userId))));
        ItemRequest itemRequest = repository.findById(itemRequestId).orElseThrow(
                () -> new ItemRequestNotFound(String.format("Item request id=%s not found", itemRequestId)));
        List<ItemDto> itemDtosOfRequest = itemRepository.findAllByRequestId(itemRequest.getId(), Sort.unsorted())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemDtosOfRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOwnItemRequests(int requesterId) {
        log.info("Get Own Item Requests received");
        log.debug("Check if user exists");
        Optional.of(userRepository.findById(requesterId).orElseThrow(
                () -> new NotFoundException(String.format("User id=%s not found", requesterId))));
        log.debug("User check ok. Getting itemRequestList from itemRequestRepository");
        List<ItemRequest> itemRequestList = repository.findAllByRequesterId(requesterId, Sort.by(Sort.Direction.DESC,
                "created"));
        return getItemDtosForRequest(itemRequestList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequests(int userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<ItemRequest> requests = repository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageRequest).getContent();
        return getItemDtosForRequest(requests);
    }

    private List<ItemRequestDto> getItemDtosForRequest(List<ItemRequest> itemRequestList) {
        log.debug("Getting itemsOfRequestsList");
        List<Item> itemsOfRequestsList = itemRepository.findAllByRequestIdIn(
                itemRequestList.stream().map(ItemRequest::getId).collect(toList())); // get all items for all requests
        List<ItemDto> itemDtosOfCurrentRequest = new ArrayList<>(); // will be used for items to request mapping
        List<ItemRequestDto> result = new ArrayList<>(); // will contain the final requests list
        Map<Integer, List<Item>> itemsOfRequestMap = itemsOfRequestsList.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        log.debug("itemRequestList got {} records", itemRequestList.size());
        log.debug("itemsOfRequestsList got {} records", itemsOfRequestsList.size());
        for (ItemRequest ir : itemRequestList) {
            if (itemsOfRequestMap.containsKey(ir.getId())) {
                itemDtosOfCurrentRequest.addAll(itemsOfRequestMap.get(ir.getId())
                        .stream()
                        .map(itemMapper::toItemDto)
                        .collect(Collectors.toList()));
            }
            result.add(ItemRequestMapper.toItemRequestDto(ir, itemDtosOfCurrentRequest));
        }
        return result;
    }
}
