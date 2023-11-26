package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByRequesterId(int requesterId, Sort sort);

    Page<ItemRequest> findAllByRequesterIdNot(int userId, Pageable pageable);

    Page<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(int userId, PageRequest pageRequest);
}
