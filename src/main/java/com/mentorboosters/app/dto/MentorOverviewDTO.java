package com.mentorboosters.app.dto;

import com.mentorboosters.app.enumUtil.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MentorOverviewDTO {

    private Long mentorId;

    private String mentorName;

    private String joinDate;

    private Long futureSessions;

    private Long completedSessions;

    private AccountStatus accountStatus;
}
