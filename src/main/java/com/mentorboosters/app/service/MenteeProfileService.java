package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.MenteeProfileDTO;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class MenteeProfileService {

    private final MenteeProfileRepository menteeProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final MentorProfileRepository mentorNewRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public CommonResponse<MenteeProfile> registerMentee(MenteeProfileDTO menteeDto) throws UnexpectedServerException {
        try {
            // Check if email or phone already exists in mentee or users table
            if (menteeProfileRepository.existsByEmailOrPhone(menteeDto.getEmail(), menteeDto.getPhone())) {
                throw new ResourceAlreadyExistsException("Email or phone number already exists!");
            }

            if (mentorNewRepository.existsByEmail(menteeDto.getEmail())) {
                throw new ResourceAlreadyExistsException("You have already registered as a mentor with this email.");
            }

            // Validate required fields
            if (menteeDto.getTimezone() == null || menteeDto.getTimezone().isBlank()) {
                throw new InvalidFieldValueException("Timezone is required.");
            }

            // Encrypt password
            String hashedPassword = passwordEncoder.encode(menteeDto.getPassword());

            // Create and save MenteeProfile entity
            MenteeProfile mentee = MenteeProfile.builder()
                    .name(menteeDto.getName())
                    .email(menteeDto.getEmail())
                    .phone(menteeDto.getPhone())
                    .password(hashedPassword)
                    .description(menteeDto.getDescription())
                    .goals(menteeDto.getGoals())
                    .timeZone(menteeDto.getTimezone())
                    .profileUrl(menteeDto.getProfileUrl())
                    .status("Active")
                    .build();

            MenteeProfile savedMentee = menteeProfileRepository.save(mentee);

            // Save entry to users table
            Users user = Users.builder()
                    .emailId(mentee.getEmail())
                    .password(hashedPassword)
                    .role(Role.USER) // Assuming mentee is assigned USER role
                    .build();

            usersRepository.save(user);

            // You can add email sending here if required
            // mailService.sendWelcomeToMentee(mentee);

            return CommonResponse.<MenteeProfile>builder()
                    .status(true)
                    .message("Mentee registered successfully")
                    .statusCode(SUCCESS_CODE)
                    .data(savedMentee)
                    .build();

        } catch (ResourceAlreadyExistsException | InvalidFieldValueException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException("Mentee registration failed: " + e.getMessage());
        }
    }


    public CommonResponse<MenteeProfileDTO> getMenteeProfile(Long id) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            MenteeProfile mentee = menteeProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Mentee not found with the id: " + id));

            var menteeProfileDTO = MenteeProfileDTO.builder()
                    .menteeId(mentee.getId())
                    .name(mentee.getName())
                    .email(mentee.getEmail())
                    .phone(mentee.getPhone())
                    .languages(mentee.getLanguages())
                    .timezone(mentee.getTimeZone())
                    .profileUrl(mentee.getProfileUrl())
                    .subscriptionPlan(mentee.getSubscriptionPlan())
                    .customerId(mentee.getId())
                    .joinDate(mentee.getCreatedAt().toLocalDate().toString())
                    .industry(mentee.getIndustry())
                    .location(mentee.getLocation())
                    .goals(mentee.getGoals())
                    .status(mentee.getStatus())
                    .build();

            return CommonResponse.<MenteeProfileDTO>builder()
                    .status(STATUS_TRUE)
                    .message("Loaded mentor profile details")
                    .statusCode(SUCCESS_CODE)
                    .data(menteeProfileDTO)
                    .build();

        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException("Error while loading mentor profile details: " + e.getMessage());
        }
    }

    @Transactional
    public CommonResponse<MenteeProfileDTO> updateMenteeProfile(Long id, MenteeProfileDTO dto) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            MenteeProfile mentee = menteeProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Mentee not found with id: " + id));

            // Only update non-null fields
            if (dto.getName() != null) mentee.setName(dto.getName());
            if (dto.getEmail() != null) mentee.setEmail(dto.getEmail());
            if (dto.getPhone() != null) mentee.setPhone(dto.getPhone());
            if (dto.getProfileUrl() != null) mentee.setProfileUrl(dto.getProfileUrl());
            if (dto.getLanguages() != null) mentee.setLanguages(dto.getLanguages());
            if (dto.getTimezone() != null) mentee.setTimeZone(dto.getTimezone());
            if (dto.getSubscriptionPlan() != null) mentee.setSubscriptionPlan(dto.getSubscriptionPlan());
            if (dto.getIndustry() != null) mentee.setIndustry(dto.getIndustry());
            if (dto.getLocation() != null) mentee.setLocation(dto.getLocation());
            if (dto.getGoals() != null) mentee.setGoals(dto.getGoals());
            if (dto.getStatus() != null) mentee.setStatus(dto.getStatus());

            MenteeProfile updated = menteeProfileRepository.save(mentee);

            // Convert createdAt to date string (e.g. "2025-05-01")
            String joinDate = updated.getCreatedAt() != null ? updated.getCreatedAt().toLocalDate().toString() : null;

            MenteeProfileDTO responseDto = MenteeProfileDTO.builder()
                    .name(updated.getName())
                    .email(updated.getEmail())
                    .phone(updated.getPhone())
                    .languages(updated.getLanguages())
                    .timezone(updated.getTimeZone())
                    .profileUrl(updated.getProfileUrl())
                    .subscriptionPlan(updated.getSubscriptionPlan())
                    .customerId(updated.getId()) // Assuming customerId = id
                    .joinDate(joinDate)
                    .industry(updated.getIndustry())
                    .location(updated.getLocation())
                    .goals(updated.getGoals())
                    .status(updated.getStatus())
                    .build();

            return CommonResponse.<MenteeProfileDTO>builder()
                    .status(true)
                    .statusCode(200)
                    .message("Mentee profile updated successfully")
                    .data(responseDto)
                    .build();

        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException("Failed to update mentee profile: " + e.getMessage());
        }
    }

}
