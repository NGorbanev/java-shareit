package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Item {
    @NotNull int id;
    @NotNull(message = "Item.name is null") String name;
    @NotNull(message = "Item.description is null") String description;
    @NotNull(message = "Item.available is null") Boolean available;
    int requestId;
}
