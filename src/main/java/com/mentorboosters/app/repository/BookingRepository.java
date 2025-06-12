package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
//    List<Booking> findByMentorIdAndBookingDate(Long mentorId, LocalDate date);
//
//    List<Booking> findByUserIdAndBookingDate(Long userId, LocalDate bookingDate);
//
//    List<Booking> findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(Long userId, Long mentorId, LocalDate today);
//
//    List<Booking> findByUserIdAndBookingDateAndPaymentStatus(Long userId, LocalDate bookingDate, String complete);

    Booking findByStripeSessionId(String sessionId);

//    List<Booking> findByMentorIdAndBookingDateAndPaymentStatus(Long mentorId, LocalDate date, String completed);

    List<Booking> findByMentorIdAndBookedDateBetweenAndPaymentStatus(Long mentorId, Instant utcStart, Instant utcEnd, String completed);

    List<Booking> findByMenteeIdAndBookedDateBetweenAndPaymentStatus(Long menteeId, Instant utcStart, Instant utcEnd, String completed);
}
