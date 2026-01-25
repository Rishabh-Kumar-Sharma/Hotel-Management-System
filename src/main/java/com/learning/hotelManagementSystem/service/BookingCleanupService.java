package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.repository.BookingRepository;
import com.learning.hotelManagementSystem.types.BookingStatus;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BookingCleanupService {
    @Autowired
    private BookingRepository bookingRepository;

    private static final long cleanupTime=60000;

    @Transactional
    @Scheduled(fixedRate = cleanupTime) // every 1 minute
    public void removeExpiredBookings() {
        int expiredCount=bookingRepository.expireOldBookings(BookingStatus.EXPIRED, BookingStatus.CREATED, LocalDateTime.now());
        int completedCount=bookingRepository.expireCompletedBookings(BookingStatus.CONFIRMED,BookingStatus.COMPLETED,LocalDateTime.now());
        if(expiredCount>0) {
            log.info("Expired {} bookings",expiredCount);
        }
        if(completedCount>0) {
            log.info("Completed {} bookings",completedCount);
        }
    }
}
