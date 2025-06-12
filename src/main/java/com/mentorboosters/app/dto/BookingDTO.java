package com.mentorboosters.app.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingDTO {

    private Long mentorId;

    private Long menteeId;

    private Long timeSlotId;

    private String bookingDate;

    private String stripeSessionId;

    private String category;

    private String connectMethod;

    private Double amount;

    private String currency;

    private String productName;

    private Long quantity;

    private String paymentStatus;

    private String mentorMeetLink;

    private String userMeetLink;
}
