package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;
    @NotNull(message = "ItemDto.name is null") String name;
    @NotNull(message = "ItemDto.description is null") String description;
    @NotNull(message = "ItemDto.available is null") Boolean available;
    int ownerId;
    int requestId;
}
