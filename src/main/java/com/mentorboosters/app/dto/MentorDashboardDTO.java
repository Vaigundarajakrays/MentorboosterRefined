package com.mentorboosters.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MentorDashboardDTO {

    private String sessionTime;

    private String sessionDuration;

    private String menteeName;

    private String sessionName;

    private String meetType;

    private String status;

    private String mentorMeetLink;

}
