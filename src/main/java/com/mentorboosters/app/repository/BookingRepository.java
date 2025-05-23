package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByMentorIdAndBookingDate(Long mentorId, LocalDate date);

    List<Booking> findByUserIdAndBookingDate(Long userId, LocalDate bookingDate);

    List<Booking> findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(Long userId, Long mentorId, LocalDate today);
}
