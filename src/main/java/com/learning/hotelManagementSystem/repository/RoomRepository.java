package com.learning.hotelManagementSystem.repository;

import com.learning.hotelManagementSystem.entity.Room;
import com.learning.hotelManagementSystem.types.BookingStatus;
import com.learning.hotelManagementSystem.types.RoomStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
    public Optional<Room> findByRoomNumber(int number);
    public boolean existsByRoomNumber(int number);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name="jakarta.persistence.lock.timeout", value="3000"))
    @Query("select r from Room r where r.id=:id")
    public Room findByIdForUpdate(@Param("id") long id);

    @Query("select r from Room r where r.roomStatus=:roomStatus")
    public List<Room> findAllRoomsByStatus(@Param("roomStatus") RoomStatus roomStatus);

    public List<Room> findByRoomStatusAndIdNotIn(RoomStatus roomStatus, List<Long> roomIds);

    @Query("""
            select r from Room r
            where r.roomStatus= :roomStatus
            and not exists (
                select b from Booking b
                where b.room=r
                and b.bookingStatus in (:bookingStatuses)
                and b.checkIn< :checkOut
                and b.checkOut> :checkIn
            )
            """)
    public List<Room> findAllAvailableRooms(@Param("roomStatus") RoomStatus roomStatus,
                                   @Param("bookingStatuses")List<BookingStatus> bookingStatuses,
                                   @Param("checkIn") LocalDateTime checkIn,
                                   @Param("checkOut") LocalDateTime checkOut);
}

