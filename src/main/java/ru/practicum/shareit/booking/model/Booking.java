package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "start_date")
    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @Column(name = "end_date")
    @NotNull
    @Future
    LocalDateTime end;
    @ManyToOne()
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @ToString.Exclude
    Item item;
    @ManyToOne()
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    @ToString.Exclude
    User booker;
    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
