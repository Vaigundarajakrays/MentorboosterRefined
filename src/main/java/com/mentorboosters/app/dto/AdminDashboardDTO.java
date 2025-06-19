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
public class AdminDashboardDTO {

    private Long noOfMentorApproved;

    private Long noOfMentorNotApproved;

    private Long noOfMentees;

    private List<MentorProfileDTO> mentorProfileDTOS;
}
