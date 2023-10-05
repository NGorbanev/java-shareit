package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Item {
    @NotNull int id;
    @NotNull @NotEmpty @NotBlank (message = "ItemDto.name is null or empty")
    String name;
    //@NotBlank (message = "ItemDto.description is null")
    //@NotEmpty(message = "ItemDto description must not be empty")
    String description;
    @NotNull @NotEmpty @NotBlank (message = "ItemDto.available is null") Boolean available;
    int requestId;
}
