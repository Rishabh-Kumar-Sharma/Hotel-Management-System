package com.learning.hotelManagementSystem.entity;

import com.learning.hotelManagementSystem.types.RoomStatus;
import com.learning.hotelManagementSystem.types.RoomType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name="rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name="room_status", nullable = false)
    private RoomStatus roomStatus;

    @NonNull
    @Column(name="price_per_night", nullable = false)
    private BigDecimal pricePerNight;

    @NonNull
    @Column(name="room_number", nullable = false, unique = true)
    private int roomNumber;

    @NonNull
    @Column(nullable = false)
    private int capacity;

    @NonNull
    @Enumerated(EnumType.STRING) // store the STRING value only instead of ORDINAL(index of the values)
    private RoomType type;



    @OneToMany(mappedBy = "room")
    private List<Booking> bookingList;

    public Room(BigDecimal pricePerNight, int roomNumber, int capacity, RoomType roomType) {
        this.pricePerNight=pricePerNight;
        this.roomNumber=roomNumber;
        this.capacity=capacity;
        this.type=roomType;
        this.roomStatus=RoomStatus.ACTIVE;
    }
}
