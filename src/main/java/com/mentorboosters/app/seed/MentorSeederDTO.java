package com.mentorboosters.app.seed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MentorSeederDTO {

    private String mentorEmail;
    private String phone;
    private String timezone;
    private String password;
    private String name;
    private String profileUrl;
    private String yearsOfExperience;
    private List<String> categories;
    private String summary;
    private String description;
    private Double amount;
    private List<String> timeSlots;
}
