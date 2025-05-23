package com.mentorboosters.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorDTO {

    private Long id;
    private String name;
    private String email;
    private String gender;
    private String avatarUrl;
    private String bio;
    private String role;
    private Double freePrice;
    private String freeUnit;
    private Boolean verified;
    private Double rate;
    private Integer numberOfMentoree;

    private List<ExperienceDTO> experiences;
    private List<CertificateDTO> certificates;
    private List<CategoryDTO> categories;
    private List<FixedTimeSlotDTO> timeSlots;

}
