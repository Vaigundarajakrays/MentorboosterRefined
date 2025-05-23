package com.mentorboosters.app.dto;

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

    private Long id;

    private Long mentorId;

    private String mentorName;

    private Long userId;

    private String userName;

    private LocalTime timeSlotStart;

    private LocalTime timeSlotEnd;

    private LocalDate bookingDate;

    private String category;

    private String connectMethod;

    private String gMeetLink;
}
