package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;
    @NotNull(message = "Item.name is null")
    @NotBlank(message = "Item name must not be empty")
    String name;
    @NotNull(message = "Item.description is null")
    @NotBlank(message = "Item description must no be empty")
    String description;
    @NotNull(message = "Item.available is null")
    Boolean available;
    int requestId;
    private List<CommentDto> comments;
}
