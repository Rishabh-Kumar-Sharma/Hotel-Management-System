package com.learning.hotelManagementSystem.entity;

import com.learning.hotelManagementSystem.types.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name="bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(name = "check_in")
    private Instant checkIn;

    @NonNull
    @Column(name="check_out")
    private Instant checkOut;

    @NonNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @NonNull
    @ManyToOne(optional = false, fetch=FetchType.LAZY)
    @JoinColumn(name="customer_id", nullable = false)
    private Customer customer;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id")
    private Room room;

    @Column(updatable = false)
    private Instant createdAt;

    @Column(name="expires_at")
    private Instant expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt=Instant.now();
        if(this.bookingStatus==null) {
            this.bookingStatus=BookingStatus.CREATED;
        }
        if(this.expiresAt==null) {
            this.expiresAt = this.createdAt.plus(15, ChronoUnit.MINUTES);
        }
    }
}
