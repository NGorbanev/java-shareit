package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @NotNull @NotEmpty @NotBlank (message = "ItemDto.name is null or empty")
    String name;
    String description;
    @NotNull (message = "ItemDto.available is null")
    Boolean available;
    @OneToOne
    @JoinColumn(name = "owner_id")
    //int ownerId;
    User owner;
    int requestId;
}
