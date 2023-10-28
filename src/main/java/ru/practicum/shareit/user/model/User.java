package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    String email;
}
