package com.mentorboosters.app.mapper;

import com.mentorboosters.app.dto.*;
import com.mentorboosters.app.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    public static MentorDTO toMentorDTO(Mentor mentor){

        List<CertificateDTO> certificateDTOS = mentor.getCertificates().stream().map(DtoMapper::toCertificateDTO).toList();
        List<ExperienceDTO> experienceDTOS = mentor.getExperiences().stream().map(DtoMapper::toExperienceDTO).toList();
        List<CategoryDTO> categoryDTOS = mentor.getCategories().stream().map(DtoMapper::toCategoryDTO).toList();
        List<FixedTimeSlotDTO> fixedTimeSlotDTOs = mentor.getTimeSlots().stream().map(DtoMapper::toFixedTimeSlotDTO).toList();

        return MentorDTO.builder()
                .id(mentor.getId())
                .name(mentor.getName())
                .email(mentor.getEmail())
                .gender(mentor.getGender())
                .avatarUrl(mentor.getAvatarUrl())
                .bio(mentor.getBio())
                .role(mentor.getRole())
                .freePrice(mentor.getFreePrice())
                .freeUnit(mentor.getFreeUnit())
                .verified(mentor.getVerified())
                .rate(mentor.getRate())
                .numberOfMentoree(mentor.getNumberOfMentoree())
                .certificates(certificateDTOS)
                .experiences(experienceDTOS)
                .categories(categoryDTOS)
                .timeSlots(fixedTimeSlotDTOs)
                .build();



    }

    public static ExperienceDTO toExperienceDTO(Experience experience) {

        return ExperienceDTO.builder()
                .id(experience.getId())
                .role(experience.getRole())
                .companyName(experience.getCompanyName())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .description(experience.getDescription())
                .build();
    }

    public static CertificateDTO toCertificateDTO(Certificate certificate){

        return CertificateDTO.builder()
                .id(certificate.getId())
                .name(certificate.getName())
                .provideBy(certificate.getProvideBy())
                .createDate(certificate.getCreateDate())
                .imageUrl(certificate.getImageUrl())
                .build();
    }

    public static CategoryDTO toCategoryDTO(Category category){

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }

    public static FixedTimeSlotDTO toFixedTimeSlotDTO(FixedTimeSlot fixedTimeSlot){

        return FixedTimeSlotDTO.builder()
                .id(fixedTimeSlot.getId())
                .timeStart(fixedTimeSlot.getTimeStart())
                .timeEnd(fixedTimeSlot.getTimeEnd())
                .build();
    }

    public static Mentor toMentorEntity(MentorDTO mentorDTO) {

        List<Certificate> certificates = mentorDTO.getCertificates().stream().map(DtoMapper::toCertificateEntity).toList();
        List<Experience> experiences = mentorDTO.getExperiences().stream().map(DtoMapper::toExperienceEntity).toList();
        List<Category> categories = mentorDTO.getCategories().stream().map(DtoMapper::toCategoryEntity).toList();
        List<FixedTimeSlot> timeSlots = mentorDTO.getTimeSlots().stream().map(DtoMapper::toFixedTimeSlotEntity).toList();

        return Mentor.builder()
                .id(mentorDTO.getId())
                .name(mentorDTO.getName())
                .email(mentorDTO.getEmail())
                .gender(mentorDTO.getGender())
                .avatarUrl(mentorDTO.getAvatarUrl())
                .bio(mentorDTO.getBio())
                .role(mentorDTO.getRole())
                .freePrice(mentorDTO.getFreePrice())
                .freeUnit(mentorDTO.getFreeUnit())
                .verified(mentorDTO.getVerified())
                .rate(mentorDTO.getRate())
                .numberOfMentoree(mentorDTO.getNumberOfMentoree())
                .certificates(certificates)
                .experiences(experiences)
                .categories(categories)
                .timeSlots(timeSlots)
                .build();
    }

    public static Experience toExperienceEntity(ExperienceDTO experienceDTO) {
        return Experience.builder()
                .id(experienceDTO.getId())
                .role(experienceDTO.getRole())
                .companyName(experienceDTO.getCompanyName())
                .startDate(experienceDTO.getStartDate())
                .endDate(experienceDTO.getEndDate())
                .description(experienceDTO.getDescription())
                .build();
    }

    public static Certificate toCertificateEntity(CertificateDTO certificateDTO) {
        return Certificate.builder()
                .id(certificateDTO.getId())
                .name(certificateDTO.getName())
                .provideBy(certificateDTO.getProvideBy())
                .createDate(certificateDTO.getCreateDate())
                .imageUrl(certificateDTO.getImageUrl())
                .build();
    }

    public static Category toCategoryEntity(CategoryDTO categoryDTO) {
        return Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .build();
    }

    public static FixedTimeSlot toFixedTimeSlotEntity(FixedTimeSlotDTO fixedTimeSlotDTO) {
        return FixedTimeSlot.builder()
                .id(fixedTimeSlotDTO.getId())
                .timeStart(fixedTimeSlotDTO.getTimeStart())
                .timeEnd(fixedTimeSlotDTO.getTimeEnd())
                .build();
    }







}
