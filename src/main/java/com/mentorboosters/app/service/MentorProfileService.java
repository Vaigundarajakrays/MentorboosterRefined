package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.AllMentorsResponseDTO;
import com.mentorboosters.app.dto.MenteeDashboardDTO;
import com.mentorboosters.app.dto.MentorDashboardDTO;
import com.mentorboosters.app.dto.MentorProfileDTO;
import com.mentorboosters.app.enumUtil.AccountStatus;
import com.mentorboosters.app.enumUtil.ApprovalStatus;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.*;
import com.mentorboosters.app.repository.BookingRepository;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.CommonFiles;
import com.mentorboosters.app.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    private final BookingRepository bookingRepository;

    //@Transactional
    //Spring only rolls back for unchecked exceptions (aka runtime exceptions) unless you explicitly tell it otherwise.
    @Transactional
    public CommonResponse<String> registerMentor(MentorProfileDTO mentorDto) throws UnexpectedServerException {
        try {
            if (mentorNewRepository.existsByEmailOrPhone(mentorDto.getEmail(), mentorDto.getPhone())) {
                throw new ResourceAlreadyExistsException(EMAIL_PHONE_EXISTS);
            }

            if (menteeProfileRepository.existsByEmail(mentorDto.getEmail())){
                throw new ResourceAlreadyExistsException(ALREADY_REGISTERED_EMAIL);
            }

            String timezone = mentorDto.getTimezone();
            if (timezone == null || timezone.isBlank()) {
                throw new InvalidFieldValueException(TIMEZONE_REQUIRED);
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
                    throw new InvalidFieldValueException(INVALID_TIME_TIMESLOTS + slotStr);
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

            return CommonResponse.<String>builder()
                    .message(REGISTERED_SUCCESSFULLY)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .data("Role: " + user.getRole().toString())
                    .build();

        } catch (ResourceAlreadyExistsException | InvalidFieldValueException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException(REGISTRATION_FAILED + e.getMessage());
        }
    }

    public CommonResponse<MentorProfileDTO> getProfileDetails(Long id) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            MentorProfile mentorNew = mentorNewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MENTOR_NOT_FOUND_ID + id));

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
                    .description(mentorNew.getDescription())
                    .resumeUrl(mentorNew.getResumeUrl())
                    .yearsOfExperience(mentorNew.getYearsOfExperience())
                    .termsAndConditions(mentorNew.getTermsAndConditions())
                    .phone(mentorNew.getPhone())
                    .profileUrl(mentorNew.getProfileUrl())
                    .timezone(mentorNew.getTimezone())
                    .timeSlots(timeSlots)
                    .accountStatus(mentorNew.getAccountStatus())
                    .build();


            return CommonResponse.<MentorProfileDTO>builder()
                    .status(STATUS_TRUE)
                    .message(DETAILS_LOADED_SUCCESSFULLY)
                    .data(mentorDto)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException(LOADING_MENTOR_DETAILS + e.getMessage());
        }
    }

    //We are getting only partial details not the whole obj, that's why we used patch
    @Transactional
    public CommonResponse<MentorProfileDTO> updateMentorProfile(Long id, MentorProfileDTO mentorDto)
            throws ResourceNotFoundException, UnexpectedServerException {
        try {
            MentorProfile mentor = mentorNewRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(MENTOR_NOT_FOUND_ID+ id));

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
                                throw new InvalidFieldValueException(INVALID_TIME_FORMAT + timeStr);
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

            // Prepare response DTO for mentor
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
                    .accountStatus(updatedMentor.getAccountStatus())
                    .build();

            return CommonResponse.<MentorProfileDTO>builder()
                    .status(true)
                    .message(PROFILE_UPDATED_SUCCESSFULLY)
                    .statusCode(SUCCESS_CODE)
                    .data(responseDto)
                    .build();

        } catch (ResourceNotFoundException | InvalidFieldValueException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException(FAILED_UPDATE_PROFILE + e.getMessage());
        }
    }


    public CommonResponse<List<MentorDashboardDTO>> getAppointments(Long mentorId) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            MentorProfile mentor = mentorNewRepository.findById(mentorId).orElseThrow(() -> new ResourceNotFoundException(MENTOR_NOT_FOUND_ID + mentorId));

            List<Booking> bookings = bookingRepository.findByMentorIdAndPaymentStatus(mentorId, COMPLETED);

            if (bookings.isEmpty()) {
                return CommonResponse.<List<MentorDashboardDTO>>builder()
                        .status(STATUS_TRUE)
                        .statusCode(SUCCESS_CODE)
                        .message(DO_NOT_HAVE_ANY_APPOINTMENTS)
                        .data(List.of())
                        .build();
            }

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

            Instant timeNow = Instant.now();

            // I didn't used stream because
            // The issue is that you're using orElseThrow(...) inside a lambda in a stream() — and that method throws a checked exception (ResourceNotFoundException).
            // Java doesn't allow checked exceptions to be thrown from inside lambda expressions unless you handle them.

            List<MentorDashboardDTO> appointments = new ArrayList<>();
            for (Booking booking : bookings) {

                // To find mentee name
                MenteeProfile mentee = menteeProfileRepository.findById(booking.getMenteeId())
                        .orElseThrow(() -> new ResourceNotFoundException(MENTEE_NOT_FOUND_ID + booking.getMenteeId() + BOOKING_ID + booking.getId()));

                // To find the status
                Instant sessionStartTime = booking.getSessionStartTime();
                Instant sessionEndTime = sessionStartTime.plus(Duration.ofMinutes(60));
                String status;
                if (timeNow.isBefore(sessionStartTime)) {
                    status = UPCOMING;
                } else if (timeNow.isAfter(sessionEndTime)) {
                    status = COMPLETE;
                } else {
                    status = ONGOING;
                }


                // To find session time in mentor time zone
                ZonedDateTime sessionTime = sessionStartTime.atZone(ZoneId.of(mentor.getTimezone()));
                String session = sessionTime.format(formatter);

                appointments.add(MentorDashboardDTO.builder()
                        .menteeName(mentee.getName())
                        .sessionTime(session)
                        .sessionName(booking.getCategory())
                        .sessionDuration("1 Hr")
                        .meetType(booking.getConnectMethod())
                        .status(status)
                        .mentorMeetLink(booking.getMentorMeetLink())
                        .build());
            }

            return CommonResponse.<List<MentorDashboardDTO>>builder()
                    .statusCode(SUCCESS_CODE)
                    .message(LOADED_MENTOR_APPOINTMENTS)
                    .data(appointments)
                    .status(STATUS_TRUE)
                    .build();



        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_LOADING_APPOINTMENTS + e.getMessage());
        }
    }

//    public CommonResponse<List<MentorProfileDTO>> getMentorsByCategoryName(String categoryName) {
//
//        // 1. Fetch all mentors
//        List<MentorProfile> allMentors = mentorNewRepository.findAll();
//
//        // 2. Filter mentors manually based on category string (case-insensitive)
//        List<MentorProfile> filtered = allMentors.stream()
//                .filter(m -> m.getCategories() != null &&
//                        m.getCategories().stream()
//                                .map(String::trim)
//                                .anyMatch(c -> c.equalsIgnoreCase(categoryName.trim())))
//                .toList();
//
//        // 3. Map to DTOs
//        List<MentorProfileDTO> dtos = filtered.stream()
//                .map(this::mapToDTO)
//                .toList();
//
//        return CommonResponse.<List<MentorProfileDTO>>builder()
//                .message("Mentors filtered by category: " + categoryName)
//                .status(true)
//                .statusCode(200)
//                .data(dtos)
//                .build();
//    }

    // It is returning timeslot as null
//    private MentorProfileDTO mapToDTO(MentorProfile mentor) {
//
//        var formatter = DateTimeFormatter.ofPattern("HH:mm");
//
//        return MentorProfileDTO.builder()
//                .mentorId(mentor.getId())
//                .name(mentor.getName())
////                .phone(mentor.getPhone())
////                .email(mentor.getEmail())
////                .linkedinUrl(mentor.getLinkedinUrl())
//                .profileUrl(mentor.getProfileUrl())
////                .resumeUrl(mentor.getResumeUrl())
////                .yearsOfExperience(mentor.getYearsOfExperience())
//                .categories(mentor.getCategories())
//                .summary(mentor.getSummary())
//                .description(mentor.getDescription())
////                .amount(mentor.getAmount())
////                .terms(mentor.getTerms())
////                .termsAndConditions(mentor.getTermsAndConditions())
////                .timezone(mentor.getTimezone())
////                .accountStatus(mentor.getAccountStatus())
////                .approvalStatus(mentor.getApprovalStatus())
////                .timeSlots(null)
//                .build();
//    }

    // it is returning timeslot as null
    // It returns only approved mentors, other apis may not, clarify
    public CommonResponse<List<AllMentorsResponseDTO>> getAllMentors() throws UnexpectedServerException {

        try {

            List<MentorProfile> mentors = mentorNewRepository.findAllByApprovalStatusAndAccountStatus(ApprovalStatus.ACCEPTED, AccountStatus.ACTIVE);

            if (mentors.isEmpty()) {
                return CommonResponse.<List<AllMentorsResponseDTO>>builder()
                        .status(STATUS_FALSE)
                        .statusCode(SUCCESS_CODE)
                        .message("No mentors found")
                        .data(List.of())
                        .build();
            }

            List<AllMentorsResponseDTO> dtos = mentors.stream()
                    .map(this::toAllMentorsResponseDTO)
                    .toList();

            return CommonResponse.<List<AllMentorsResponseDTO>>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("Mentors fetched successfully")
                    .data(dtos)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException("Error while loading mentors:" + e.getMessage());
        }

    }

    private AllMentorsResponseDTO toAllMentorsResponseDTO(MentorProfile mentorProfile){

        return AllMentorsResponseDTO.builder()
                .mentorId(mentorProfile.getId())
                .name(mentorProfile.getName())
                .profileUrl(mentorProfile.getProfileUrl())
                .categories(mentorProfile.getCategories())
                .summary(mentorProfile.getSummary())
                .build();
    }
}
