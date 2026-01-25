package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.DTO.RoomDTO.CreateRoomRequest;
import com.learning.hotelManagementSystem.DTO.RoomDTO.CreateRoomResponse;
import com.learning.hotelManagementSystem.DTO.RoomDTO.GetAllAvailableRoomsResponse;
import com.learning.hotelManagementSystem.DTO.RoomDTO.RoomResponse;
import com.learning.hotelManagementSystem.entity.Room;
import com.learning.hotelManagementSystem.exceptions.DuplicateEntityException;
import com.learning.hotelManagementSystem.repository.BookingRepository;
import com.learning.hotelManagementSystem.repository.RoomRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import com.learning.hotelManagementSystem.types.BookingStatus;
import com.learning.hotelManagementSystem.types.RoomStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;

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
    public void updateRoomDetails(long id, Room newRoomDetails) {
        Room room=roomRepository.findById(id).orElseThrow(()->new EntityNotFoundException(Translations.ROOM_NOT_FOUND));

        if(newRoomDetails.getRoomNumber()!=room.getRoomNumber()) {
            if(roomRepository.existsByRoomNumber(newRoomDetails.getRoomNumber())) {
                throw new DuplicateEntityException(Translations.ROOM_ALREADY_EXISTS);
            }
            room.setRoomNumber(newRoomDetails.getRoomNumber());
        }

        room.setPricePerNight(newRoomDetails.getPricePerNight());
        room.setCapacity(newRoomDetails.getCapacity());
        room.setType(newRoomDetails.getType());
        room.setRoomStatus(newRoomDetails.getRoomStatus());

//        No need to call 'save' explicitly as the persistence object has become 'dirty' => dirty check in a "Transaction"
    }

    public GetAllAvailableRoomsResponse getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut) {
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
}
