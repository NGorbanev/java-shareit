package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.Collection;


@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Collection<User> findByEmail(String email);
}
