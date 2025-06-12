package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.MentorProfileDTO;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.FixedTimeSlotNew;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.CommonFiles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class MentorProfileService {

    private final MentorProfileRepository mentorNewRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final CommonFiles commonFiles;
    private final MenteeProfileRepository menteeProfileRepository;

    //@Transactional
    //Spring only rolls back for unchecked exceptions (aka runtime exceptions) unless you explicitly tell it otherwise.
    @Transactional
    public CommonResponse<MentorProfile> registerMentor(MentorProfileDTO mentorDto) throws UnexpectedServerException {
        try {
            if (mentorNewRepository.existsByEmailOrPhone(mentorDto.getEmail(), mentorDto.getPhone())) {
                throw new ResourceAlreadyExistsException("Email or phone number already exists!");
            }

            if (menteeProfileRepository.existsByEmail(mentorDto.getEmail())){
                throw new ResourceAlreadyExistsException("You have already registered as a mentee with this email");
            }

            String timezone = mentorDto.getTimezone();
            if (timezone == null || timezone.isBlank()) {
                throw new InvalidFieldValueException("Timezone is required.");
            }

            // Convert DTO to entity
            MentorProfile mentor = MentorProfile.builder()
                    .name(mentorDto.getName())
                    .phone(mentorDto.getPhone())
                    .email(mentorDto.getEmail())
                    .linkedinUrl(mentorDto.getLinkedinUrl())
                    .profileUrl(mentorDto.getProfileUrl())
                    .resumeUrl(mentorDto.getResumeUrl())
                    .yearsOfExperience(mentorDto.getYearsOfExperience())
                    .password(mentorDto.getPassword()) // Will hash below
                    .categories(mentorDto.getCategories())
                    .summary(mentorDto.getSummary())
                    .amount(mentorDto.getAmount())
                    .terms(mentorDto.getTerms())
                    .termsAndConditions(mentorDto.getTermsAndConditions())
                    .timezone(timezone)
                    .build();

            List<FixedTimeSlotNew> timeSlots = mentorDto.getTimeSlots().stream().map(slotStr -> {
                try {
                    String trimmed = slotStr.trim(); // Clean up extra spaces
                    LocalTime localTime = LocalTime.parse(trimmed); // Parse "HH:mm"
                    LocalDate localDate = LocalDate.now(ZoneId.of(timezone)); // Use today in mentor's timezone
                    ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate, localTime, ZoneId.of(timezone));
                    Instant utcInstant = zonedDateTime.toInstant();

                    return FixedTimeSlotNew.builder()
                            .timeStart(utcInstant)
                            .mentor(mentor)
                            .build();
                } catch (DateTimeParseException e) {
                    throw new InvalidFieldValueException("Invalid time format in timeSlots: " + slotStr);
                }
            }).toList();

            mentor.setTimeSlots(timeSlots);

            // Encrypt password
            String hashedPassword = passwordEncoder.encode(mentorDto.getPassword());
            mentor.setPassword(hashedPassword);

            // Save mentor
            MentorProfile savedMentor = mentorNewRepository.save(mentor);

            // Create and save user
            Users user = Users.builder()
                    .emailId(mentor.getEmail())
                    .role(Role.MENTOR)
                    .password(hashedPassword)
                    .build();
            usersRepository.save(user);

            // Send welcome mail
//            commonFiles.sendPasswordToMentorNew(mentor, mentorDto.getPassword());

            return CommonResponse.<MentorProfile>builder()
                    .message("Mentor registered successfully")
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .data(savedMentor)
                    .build();

        } catch (ResourceAlreadyExistsException | InvalidFieldValueException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException("Registration failed: " + e.getMessage());
        }
    }

    public CommonResponse<MentorProfileDTO> getProfileDetails(Long id) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            MentorProfile mentorNew = mentorNewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Mentor not found with id: " + id));

            List<String> timeSlots = mentorNew.getTimeSlots().stream()
                    .map(slot -> slot.getTimeStart()
                            .atZone(ZoneId.of(mentorNew.getTimezone()))
                            .toLocalTime()
                            .toString())
                    .toList();


            var mentorDto = MentorProfileDTO.builder()
                    .mentorId(mentorNew.getId())
                    .name(mentorNew.getName())
                    .email(mentorNew.getEmail())
                    .amount(mentorNew.getAmount())
                    .profileUrl(mentorNew.getProfileUrl())
                    .categories(mentorNew.getCategories())
                    .linkedinUrl(mentorNew.getLinkedinUrl())
                    .terms(mentorNew.getTerms())
                    .summary(mentorNew.getSummary())
                    .resumeUrl(mentorNew.getResumeUrl())
                    .yearsOfExperience(mentorNew.getYearsOfExperience())
                    .termsAndConditions(mentorNew.getTermsAndConditions())
                    .phone(mentorNew.getPhone())
                    .profileUrl(mentorNew.getProfileUrl())
                    .timezone(mentorNew.getTimezone())
                    .timeSlots(timeSlots)
                    .status("Active")
                    .build();


            return CommonResponse.<MentorProfileDTO>builder()
                    .status(STATUS_TRUE)
                    .message("Mentor details loaded successfully")
                    .data(mentorDto)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException("Error while loading mentor details: " + e.getMessage());
        }
    }

    //We are getting only partial details not the whole obj, that's why we used patch
    @Transactional
    public CommonResponse<MentorProfileDTO> updateMentorProfile(Long id, MentorProfileDTO mentorDto)
            throws ResourceNotFoundException, UnexpectedServerException {
        try {
            MentorProfile mentor = mentorNewRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with id: " + id));

            // Update only non-null fields (Partial update / PATCH style)
            if (mentorDto.getName() != null) mentor.setName(mentorDto.getName());
            if (mentorDto.getPhone() != null) mentor.setPhone(mentorDto.getPhone());
            if (mentorDto.getEmail() != null) mentor.setEmail(mentorDto.getEmail());
            if (mentorDto.getLinkedinUrl() != null) mentor.setLinkedinUrl(mentorDto.getLinkedinUrl());
            if (mentorDto.getProfileUrl() != null) mentor.setProfileUrl(mentorDto.getProfileUrl());
            if (mentorDto.getResumeUrl() != null) mentor.setResumeUrl(mentorDto.getResumeUrl());
            if (mentorDto.getYearsOfExperience() != null) mentor.setYearsOfExperience(mentorDto.getYearsOfExperience());
            if (mentorDto.getCategories() != null) mentor.setCategories(mentorDto.getCategories());
            if (mentorDto.getSummary() != null) mentor.setSummary(mentorDto.getSummary());
            if (mentorDto.getAmount() != null) mentor.setAmount(mentorDto.getAmount());
            if (mentorDto.getTerms() != null) mentor.setTerms(mentorDto.getTerms());
            if (mentorDto.getTermsAndConditions() != null) mentor.setTermsAndConditions(mentorDto.getTermsAndConditions());
            if (mentorDto.getTimezone() != null) mentor.setTimezone(mentorDto.getTimezone());

            String effectiveTimezone = mentorDto.getTimezone() != null ? mentorDto.getTimezone() : mentor.getTimezone();

            // When updating it, we need time zone that's why we get effective timezone
            if (mentorDto.getTimeSlots() != null) {
                List<FixedTimeSlotNew> updatedTimeSlots = mentorDto.getTimeSlots().stream()
                        .map(timeStr -> {
                            try {
                                LocalTime localTime = LocalTime.parse(timeStr.trim());
                                LocalDate today = LocalDate.now(ZoneId.of(effectiveTimezone));
                                ZonedDateTime zoned = ZonedDateTime.of(today, localTime, ZoneId.of(effectiveTimezone));
                                return FixedTimeSlotNew.builder()
                                        .mentor(mentor)
                                        .timeStart(zoned.toInstant())
                                        .build();
                            } catch (DateTimeParseException e) {
                                throw new InvalidFieldValueException("Invalid time format: " + timeStr);
                            }
                        }).toList();

                mentor.getTimeSlots().clear();
                mentor.getTimeSlots().addAll(updatedTimeSlots);
            }

            MentorProfile updatedMentor = mentorNewRepository.save(mentor);

            // Build timeSlots back to String list for response
            List<String> updatedTimeSlotsStr = updatedMentor.getTimeSlots().stream()
                    .map(slot -> slot.getTimeStart()
                            .atZone(ZoneId.of(updatedMentor.getTimezone()))
                            .toLocalTime()
                            .toString())
                    .toList();

            // Prepare response DTO
            MentorProfileDTO responseDto = MentorProfileDTO.builder()
                    .name(updatedMentor.getName())
                    .phone(updatedMentor.getPhone())
                    .email(updatedMentor.getEmail())
                    .linkedinUrl(updatedMentor.getLinkedinUrl())
                    .profileUrl(updatedMentor.getProfileUrl())
                    .resumeUrl(updatedMentor.getResumeUrl())
                    .yearsOfExperience(updatedMentor.getYearsOfExperience())
                    .categories(updatedMentor.getCategories())
                    .summary(updatedMentor.getSummary())
                    .amount(updatedMentor.getAmount())
                    .terms(updatedMentor.getTerms())
                    .termsAndConditions(updatedMentor.getTermsAndConditions())
                    .timezone(updatedMentor.getTimezone())
                    .timeSlots(updatedTimeSlotsStr)
                    .status("Active")
                    .build();

            return CommonResponse.<MentorProfileDTO>builder()
                    .status(true)
                    .message("Mentor profile updated successfully")
                    .statusCode(SUCCESS_CODE)
                    .data(responseDto)
                    .build();

        } catch (ResourceNotFoundException | InvalidFieldValueException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException("Failed to update mentor profile: " + e.getMessage());
        }
    }




}
