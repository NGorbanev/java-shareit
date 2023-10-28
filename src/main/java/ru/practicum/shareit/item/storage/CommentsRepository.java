package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItem_Id(int itemId, Sort sort);
}
