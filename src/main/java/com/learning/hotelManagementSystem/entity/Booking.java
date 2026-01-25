package com.learning.hotelManagementSystem.entity;

import com.learning.hotelManagementSystem.types.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime checkIn;

    @NonNull
    @Column(name="check_out")
    private LocalDateTime checkOut;

    @NonNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name="customer_id", nullable = false)
    private Customer customer;

    @NonNull
    @ManyToOne
    @JoinColumn(name="room_id")
    private Room room;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(name="expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt=LocalDateTime.now();
        if(this.bookingStatus==null) {
            this.bookingStatus=BookingStatus.CREATED;
        }
        if(this.expiresAt==null) {
            this.expiresAt = this.createdAt.plusMinutes(15);
        }
    }
}
