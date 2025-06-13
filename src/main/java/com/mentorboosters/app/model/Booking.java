package com.mentorboosters.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "bookings")
public class Booking extends BaseEntity {

    // Create a dto include timezone and local date and session id?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mentorId;

    @Column(nullable = false)
    private Long menteeId;

    @Column(nullable = false)
    private Long timeSlotId;

    // This is the new field you'll use to store the exact booking time in UTC
    @Column(nullable = false)
    private Instant bookedDate;

    @Column(nullable = false)
    private String menteeTimezone;

    private String stripeSessionId;

    private Instant sessionStartTime;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String connectMethod;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long quantity;

    private String paymentStatus;

    @Column(columnDefinition = "TEXT")
    private String mentorMeetLink;

    @Column(columnDefinition = "TEXT")
    private String userMeetLink;
}
