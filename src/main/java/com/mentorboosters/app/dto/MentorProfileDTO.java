package com.mentorboosters.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorProfileDTO {
    private Long mentorId;
    private String name;
    private String phone;
    private String email;
    private String linkedinUrl;
    private String profileUrl;
    private String resumeUrl;
    private String yearsOfExperience;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private List<String> categories;
    private String summary;
    private Double amount;
    private Boolean terms;
    private Boolean termsAndConditions;
    private String timezone; // 👈 Needed to convert local to UTC
    private List<String> timeSlots; // 👈 List of time slot DTOs
    private String status;
}
