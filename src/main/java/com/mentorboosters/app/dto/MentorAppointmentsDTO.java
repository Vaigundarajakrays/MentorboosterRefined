package com.mentorboosters.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MentorAppointmentsDTO {

    private Long mentorId;

    private String mentorName;

    private List<MentorDashboardDTO> mentorDashboardDTOS;
}
