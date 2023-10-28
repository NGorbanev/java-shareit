package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;

    @ManyToOne()
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @ToString.Exclude
    private Item item;

    @ManyToOne()
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @ToString.Exclude
    private User author;
    private LocalDateTime created;


}
