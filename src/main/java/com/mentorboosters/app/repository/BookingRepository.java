package com.mentorboosters.app.repository;

import com.mentorboosters.app.enumUtil.PaymentStatus;
import com.mentorboosters.app.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

//    List<Booking> findByMentorIdAndBookedDateBetweenAndPaymentStatus(Long mentorId, Instant utcStart, Instant utcEnd, PaymentStatus paymentStatus);
//
//    List<Booking> findByMenteeIdAndBookedDateBetweenAndPaymentStatus(Long menteeId, Instant utcStart, Instant utcEnd, PaymentStatus paymentStatus);

    List<Booking> findByMenteeIdAndPaymentStatus(Long menteeId, PaymentStatus paymentStatus);

    List<Booking> findByMentorIdAndPaymentStatus(Long mentorId, PaymentStatus paymentStatus);

    List<Booking> findByMentorIdAndSessionStartTimeBetweenAndPaymentStatus(Long mentorId, Instant utcStart, Instant utcEnd, PaymentStatus paymentStatus);

    List<Booking> findByMenteeIdAndSessionStartTimeBetweenAndPaymentStatus(Long menteeId, Instant utcStart, Instant utcEnd, PaymentStatus paymentStatus);

//    Optional<Booking> findByTimeSlotIdAndSessionStartTimeBetweenAndPaymentStatus(Long timeSlotId, Instant utcStart, Instant utcEnd, PaymentStatus paymentStatus);

//    Optional<Booking> findByTimeSlotIdAndSessionStartTimeAndPaymentStatus(Long timeSlotId, Instant sessionStart, PaymentStatus paymentStatus);

//    Optional<Booking> findByTimeSlotIdAndSessionStartTimeAndPaymentStatusIn(Long timeSlotId, Instant sessionStart, List<PaymentStatus> completed);

    List<Booking> findByPaymentStatusAndHoldStartTimeBefore(PaymentStatus paymentStatus, Instant cutoffTime);

    List<Booking> findByTimeSlotIdAndSessionStartTimeAndPaymentStatusIn(Long timeSlotId, Instant sessionStart, List<PaymentStatus> completed);
}
