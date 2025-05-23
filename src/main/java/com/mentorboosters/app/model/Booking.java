package com.mentorboosters.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "bookings")
public class Booking extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mentorId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long timeSlotId;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String connectMethod;

    @Column(nullable = true)
    private String googleMeetLink;
}
