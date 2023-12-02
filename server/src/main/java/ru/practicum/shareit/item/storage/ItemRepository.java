package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findByOwnerIdOrderByIdAsc(int ownerId, PageRequest pageRequest);

    @Query(value = "SELECT i FROM Item i " +
            "WHERE LOWER(i.name) LIKE LOWER(concat('%', :search, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(concat('%', :search, '%')) " +
            "AND AVAILABLE = TRUE")
    List<Item> getItemsBySearchQuery(@Param("search") String text, PageRequest pageRequest);

    List<Item> findAllByRequestId(int requestId, Sort sort);

    List<Item> findAllByRequestIdIn(@Param("ids") List<Integer> ids);

}
