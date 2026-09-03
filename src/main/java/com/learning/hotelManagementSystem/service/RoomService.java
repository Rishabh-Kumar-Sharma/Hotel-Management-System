package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.RoomDTO.*;
import com.learning.hotelManagementSystem.entity.Booking;
import com.learning.hotelManagementSystem.entity.Room;
import com.learning.hotelManagementSystem.entity.User;
import com.learning.hotelManagementSystem.exceptions.DuplicateEntityException;
import com.learning.hotelManagementSystem.repository.BookingRepository;
import com.learning.hotelManagementSystem.repository.RoomRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import com.learning.hotelManagementSystem.types.BookingStatus;
import com.learning.hotelManagementSystem.types.RoomAvailabilityEnum;
import com.learning.hotelManagementSystem.types.RoomStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private final List<BookingStatus> activeStatuses=new ArrayList<>(Arrays.asList(BookingStatus.CREATED, BookingStatus.CONFIRMED));

    public CreateRoomResponse addRoom(CreateRoomRequest room) {
        if (roomRepository.existsByRoomNumber(room.roomNumber())) {
            throw new DuplicateEntityException(Translations.ROOM_ALREADY_EXISTS);
        }

        Room room1=new Room(room.pricePerNight(),room.roomNumber(),room.capacity(),room.roomType());
        roomRepository.save(room1);

        return new CreateRoomResponse(room1.getId(),room1.getPricePerNight(),room1.getRoomNumber(),room1.getCapacity(),room1.getType());
    }

    public Room getRoomDetailsById(long id) {
        return roomRepository.findById(id).orElseThrow(()->new EntityNotFoundException(Translations.ROOM_NOT_FOUND));
    }

    public void deleteRoom(long id) {
        if(!roomRepository.existsById(id)) throw new EntityNotFoundException(Translations.ROOM_NOT_FOUND);
        roomRepository.deleteById(id);
    }

    @Transactional
    public CreateRoomResponse updateRoomDetails(UpdateRoomRequest newRoomDetails) {
        final long id=newRoomDetails.id();
        Room room=roomRepository.findById(id).orElseThrow(()->new EntityNotFoundException(Translations.ROOM_NOT_FOUND));

        if(newRoomDetails.roomNumber()!=null && newRoomDetails.roomNumber()!=room.getRoomNumber()) {
            if(roomRepository.existsByRoomNumber(newRoomDetails.roomNumber())) {
                throw new DuplicateEntityException(Translations.ROOM_ALREADY_EXISTS);
            }
            room.setRoomNumber(newRoomDetails.roomNumber());
        }

        if(newRoomDetails.pricePerNight()!=null) room.setPricePerNight(newRoomDetails.pricePerNight());
        if(newRoomDetails.capacity()!=null) room.setCapacity(newRoomDetails.capacity());
        if(newRoomDetails.roomType()!=null) room.setType(newRoomDetails.roomType());
        if(newRoomDetails.roomStatus()!=null) room.setRoomStatus(newRoomDetails.roomStatus());
//        No need to call 'save' explicitly as the persistence object has become 'dirty' => dirty check in a "Transaction"

        return new CreateRoomResponse(room.getId(),room.getPricePerNight(),room.getRoomNumber(),room.getCapacity(),room.getType());
    }

    public GetAllAvailableRoomsResponse getAvailableRooms(Instant checkIn, Instant checkOut) {
        if(checkIn==null || checkOut==null || checkOut.isBefore(checkIn)) {
            throw new IllegalArgumentException(Translations.INVALID_CHECK_IN_CHECK_OUT_TIMES);
        }

        List<BookingStatus> bookingStatuses=List.of(BookingStatus.CREATED,BookingStatus.CONFIRMED);

        return new GetAllAvailableRoomsResponse(
                roomRepository.findAllAvailableRooms(RoomStatus.ACTIVE,bookingStatuses,checkIn,checkOut)
                .stream()
                .map(item->new RoomResponse(
                        item.getId(),
                        item.getPricePerNight(),
                        item.getRoomNumber(),
                        item.getCapacity(),
                        item.getType()
                ))
                .toList()
        );
    }

    public GetRoomAvailabilityResponse getAvailableRoomsWithDatesFilter(GetRoomAvailabilityRequest request) {
        final long bookingId=request.bookingId();
        final Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException(Translations.BOOKING_DOES_NOT_EXIST));
        final Instant checkIn=request.checkIn(), checkOut=request.checkOut();

        if(!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException(Translations.INVALID_CHECK_IN_CHECK_OUT_TIMES);
        }
        if(!booking.getCheckIn().isAfter(Instant.now())) {
            throw new IllegalStateException(Translations.BOOKING_ALREADY_STARTED);
        }

        final long roomId=request.roomNumber();
        final User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final List<TimeSlot> bookedSlots=bookingRepository.getBookingsByRoomId(roomId,request.checkIn(),request.checkOut(),user.getId(),activeStatuses);
        final List<TimeSlot> availableSlots=new ArrayList<>();
        Instant current=request.checkIn();
        for(TimeSlot bookedSlot:bookedSlots) {
            if(bookedSlot.checkIn().isAfter(current)) {
                availableSlots.add(new TimeSlot(current,bookedSlot.checkIn()));
            }
            if(bookedSlot.checkOut().isAfter(current)) {
                current=bookedSlot.checkOut();
            }
        }
        if(current.isBefore(request.checkOut())) {
            availableSlots.add(new TimeSlot(current,request.checkOut()));
        }

        if(availableSlots.isEmpty()) {
            return new GetRoomAvailabilityResponse(roomId, RoomAvailabilityEnum.NOT_AVAILABLE,availableSlots);
        } else if(availableSlots.size()==1 && availableSlots.get(0).checkIn().equals(request.checkIn()) &&
        availableSlots.get(0).checkOut().equals(request.checkOut())) {
            return new GetRoomAvailabilityResponse(roomId, RoomAvailabilityEnum.AVAILABLE, availableSlots);
        }
        return new GetRoomAvailabilityResponse(roomId, RoomAvailabilityEnum.PARTIALLY_AVAILABLE, availableSlots);
    }
}
