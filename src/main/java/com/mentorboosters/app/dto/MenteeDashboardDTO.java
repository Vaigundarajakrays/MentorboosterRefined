package com.mentorboosters.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MenteeDashboardDTO {

    private String sessionTime;

    private String mentorName;

    private String meetType;

    private String status;
}
