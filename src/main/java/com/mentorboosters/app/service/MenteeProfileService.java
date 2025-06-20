package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.MenteeDashboardDTO;
import com.mentorboosters.app.dto.MenteeProfileDTO;
import com.mentorboosters.app.dto.RescheduleDTO;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.*;
import com.mentorboosters.app.repository.*;
import com.mentorboosters.app.response.CommonResponse;
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
public class MenteeProfileService {

    private final MenteeProfileRepository menteeProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final MentorProfileRepository mentorNewRepository;
    private final UsersRepository usersRepository;
    private final BookingRepository bookingRepository;
    private final FixedTimeSlotNewRepository fixedTimeSlotNewRepository;

    @Transactional
    public CommonResponse<MenteeProfile> registerMentee(MenteeProfileDTO menteeDto) throws UnexpectedServerException {
        try {
            // Check if email or phone already exists in mentee or users table
            if (menteeProfileRepository.existsByEmailOrPhone(menteeDto.getEmail(), menteeDto.getPhone())) {
                throw new ResourceAlreadyExistsException(EMAIL_PHONE_EXISTS);
            }

            if (mentorNewRepository.existsByEmail(menteeDto.getEmail())) {
                throw new ResourceAlreadyExistsException(ALREADY_REGISTERED_MENTOR_EMAIL);
            }

            // Validate required fields
            if (menteeDto.getTimezone() == null || menteeDto.getTimezone().isBlank()) {
                throw new InvalidFieldValueException(TIMEZONE_REQUIRED);
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
                    .status(ACTIVE)
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
                    .message(MENTEE_REGISTER_SUCCESSFULLY)
                    .statusCode(SUCCESS_CODE)
                    .data(savedMentee)
                    .build();

        } catch (ResourceAlreadyExistsException | InvalidFieldValueException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException(MENTEE_REGISTRATION_FAILED+ e.getMessage());
        }
    }


    public CommonResponse<MenteeProfileDTO> getMenteeProfile(Long id) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            MenteeProfile mentee = menteeProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MENTEE_FOUND_THE_ID + id));

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

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
                    .joinDate(mentee.getCreatedAt().atZone(ZoneId.of(mentee.getTimeZone())).format(formatter))
                    .industry(mentee.getIndustry())
                    .location(mentee.getLocation())
                    .goals(mentee.getGoals())
                    .status(mentee.getStatus())
                    .build();

            return CommonResponse.<MenteeProfileDTO>builder()
                    .status(STATUS_TRUE)
                    .message(LOADED_PROFILE_DETAILS)
                    .statusCode(SUCCESS_CODE)
                    .data(menteeProfileDTO)
                    .build();

        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_LOADING_MENTOR_PROFILE_DETAILS + e.getMessage());
        }
    }

    @Transactional
    public CommonResponse<MenteeProfileDTO> updateMenteeProfile(Long id, MenteeProfileDTO dto) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            MenteeProfile mentee = menteeProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MENTEE_NOT_FOUND_ID + id));

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

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
            String joinDate = updated.getCreatedAt().atZone(ZoneId.of(mentee.getTimeZone())).format(formatter);

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
                    .message(MENTEE_PROFILE_UPDATED_SUCCESSFULLY)
                    .data(responseDto)
                    .build();

        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException(FAILED_UPDATE_MENTEE_PROFILE + e.getMessage());
        }
    }

    public CommonResponse<List<MenteeDashboardDTO>> getAppointments(Long menteeId) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            MenteeProfile mentee = menteeProfileRepository.findById(menteeId).orElseThrow(() -> new ResourceNotFoundException(MENTEE_NOT_FOUND_ID + menteeId));

            List<Booking> bookings = bookingRepository.findByMenteeIdAndPaymentStatus(menteeId,COMPLETED);

            if (bookings.isEmpty()) {
                return CommonResponse.<List<MenteeDashboardDTO>>builder()
                        .status(STATUS_TRUE)
                        .statusCode(SUCCESS_CODE)
                        .message(MENTEE_NOT_HAVE_ANY_APPOINTMENTS)
                        .data(List.of())
                        .build();
            }

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

            Instant timeNow = Instant.now();

            // I didn't used stream because
            // The issue is that you're using orElseThrow(...) inside a lambda in a stream() — and that method throws a checked exception (ResourceNotFoundException).
            // Java doesn't allow checked exceptions to be thrown from inside lambda expressions unless you handle them.

            List<MenteeDashboardDTO> appointments = new ArrayList<>();
            for (Booking booking : bookings) {

                // To find mentor name
                MentorProfile mentor = mentorNewRepository.findById(booking.getMentorId())
                        .orElseThrow(() -> new ResourceNotFoundException(MENTOR_NOT_FOUND_ID + booking.getMentorId() + BOOKING_ID + booking.getId()));

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


                // To find session time in mentee time zone
                ZonedDateTime sessionTime = sessionStartTime.atZone(ZoneId.of(mentee.getTimeZone()));
                String session = sessionTime.format(formatter);

                appointments.add(MenteeDashboardDTO.builder()
                        .mentorName(mentor.getName())
                        .sessionTime(session)
                        .bookingId(booking.getId())
                        .meetType(booking.getConnectMethod())
                        .status(status)
                        .build());
            }

            return CommonResponse.<List<MenteeDashboardDTO>>builder()
                    .statusCode(SUCCESS_CODE)
                    .message(LOADED_MENTEE_APPOINTMENTS)
                    .data(appointments)
                    .status(STATUS_TRUE)
                    .build();

        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_LOADING_APPOINTMENTS_MENTEE + e.getMessage());
        }

    }

//    public CommonResponse<MenteeDashboardDTO> rescheduleBooking(Long bookingId, RescheduleDTO rescheduleDTO) throws ResourceNotFoundException {
//
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with id: " + bookingId));
//
//        FixedTimeSlotNew currentSlot = fixedTimeSlotNewRepository.findById(rescheduleDTO.getTimeSlotId()).orElseThrow(()-> new ResourceNotFoundException("Time slot not found for this id: " + rescheduleDTO.getTimeSlotId()));
//
//        MentorProfile mentorProfile = mentorNewRepository.findById(booking.getMentorId()).orElseThrow(()-> new ResourceNotFoundException("Booked mentor not found for the id: " + booking.getMentorId()));
//
//        MenteeProfile menteeProfile = menteeProfileRepository.findById(booking.getMenteeId()).orElseThrow(()-> new ResourceNotFoundException("Mentee not found for the id: " + booking.getMenteeId()));
//
//        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        LocalDate date;
//        try {
//            date = LocalDate.parse(rescheduleDTO.getDate(), dateFormatter);
//        } catch (DateTimeParseException e) {
//            throw new InvalidFieldValueException("Invalid booking date format. Expected yyyy-MM-dd.");
//        }
//
//        ZoneId zoneId;
//        try{
//            zoneId=ZoneId.of(menteeProfile.getTimeZone());
//        } catch (DateTimeException e){
//            throw new InvalidFieldValueException("Invalid time zone");
//        }
//
//        Instant bookingDate = date.atStartOfDay(zoneId).toInstant();
//
//        LocalTime localTimeSlotInMenteeTimezone = currentSlot.getTimeStart().atZone(zoneId).toLocalTime();
//
//        Instant sessionStartTime = date.atTime(localTimeSlotInMenteeTimezone).atZone(zoneId).toInstant();
//
//
//
//
//
//    }
}
