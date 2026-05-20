package com.learning.hotelManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="customers")
@Builder
public class Customer {
    @Id
    private long id;

    private boolean isActive;

    @MapsId
    @OneToOne
    @JoinColumn(name="id")
    private User user;

    @OneToMany(mappedBy = "customer")
    private List<Booking> bookings;
}
