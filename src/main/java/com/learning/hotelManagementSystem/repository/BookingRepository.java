package com.learning.hotelManagementSystem.repository;

import com.learning.hotelManagementSystem.entity.Booking;
import com.learning.hotelManagementSystem.types.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query(""" 
            select b from Booking b
            where b.room.id = :roomId
            and b.bookingStatus in (:bookingStatuses)
            and b.checkIn < :checkOut
            and b.checkOut > :checkIn
            and b.expiresAt > CURRENT_TIMESTAMP
            """)
    public List<Booking> findOverlappingBookings(@Param("roomId") Long roomId,
                                                 @Param("checkIn") LocalDateTime checkInTime,
                                                 @Param("checkOut") LocalDateTime checkoutTime,
                                                 @Param("bookingStatuses") List<BookingStatus> bookingStatuses
                                                 );

    @Query("""
            select distinct b.room.id
            from Booking b
            where b.bookingStatus in (:activeStatuses)
            and b.checkIn<:checkOut
            and b.checkOut>:checkIn
            """)
    public List<Long> findBookedRoomIds(@Param("checkIn") LocalDateTime checkIn,
                                        @Param("checkOut") LocalDateTime checkOut,
                                        @Param("activeStatuses") List<BookingStatus> bookingStatuses
    );

    @Query("select b from Booking b where b.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Booking findByIdForUpdate(@Param("id") long id);

    @Modifying // this query is going to modify DB states, different from the expected behaviour of the queries(generally used
//    to read only)
    @Query("""
            update Booking b
            set b.bookingStatus = :expiredStatus
            where b.bookingStatus = :createdStatus
            and b.expiresAt <= :now
            """)
    int expireOldBookings(@Param("expiredStatus") BookingStatus expiredStatus,
                          @Param("createdStatus") BookingStatus createdStatus,
                          @Param("now") LocalDateTime now);

    @Modifying
    @Query("""
            update Booking b
            set b.bookingStatus= :completedStatus
            where b.bookingStatus= :confirmedStatus
            and b.checkOut<= :now
            """)
    int expireCompletedBookings(@Param("confirmedStatus") BookingStatus confirmedStatus,
                                @Param("completedStatus") BookingStatus completedStatus,
                                @Param("now") LocalDateTime now);
}

