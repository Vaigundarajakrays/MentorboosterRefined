package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.*;
import com.mentorboosters.app.enumUtil.AccountStatus;
import com.mentorboosters.app.enumUtil.ApprovalStatus;
import com.mentorboosters.app.enumUtil.PaymentStatus;
import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.repository.BookingRepository;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.swing.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private  final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;
    private final BookingRepository bookingRepository;


    // wanna add no of mentors active? inactive?
    public CommonResponse<AdminDashboardDTO> getAdminDashboardDetails() throws UnexpectedServerException {

        try {

            Long mentorApprovedCount = mentorProfileRepository.countByApprovalStatus(ApprovalStatus.ACCEPTED);
            Long mentorNotApprovedCount = mentorProfileRepository.countByApprovalStatus(ApprovalStatus.PENDING);

            Long noOfMentee = menteeProfileRepository.count();

            // Can use findAllByApprovalStatus too, both works
            List<MentorProfile> mentorProfiles = mentorProfileRepository.findByApprovalStatus(ApprovalStatus.PENDING);

            if (mentorProfiles.isEmpty()) {

                var adminDashboard = AdminDashboardDTO.builder()
                        .noOfMentorApproved(mentorApprovedCount)
                        .noOfMentorNotApproved(mentorNotApprovedCount)
                        .noOfMentees(noOfMentee)
                        .mentorProfileDTOS(List.of())
                        .build();

                return CommonResponse.<AdminDashboardDTO>builder()
                        .status(STATUS_FALSE)
                        .statusCode(SUCCESS_CODE)
                        .message(MENTORS_NOT_FOUND)
                        .data(adminDashboard)
                        .build();
            }

            List<MentorProfileDTO> mentorProfileDTOS = mentorProfiles.stream()
                    .map(mentorProfile -> {

                        List<String> timeslots = mentorProfile.getTimeSlots().stream()
                                .map(timeSlot -> timeSlot.getTimeStart().atZone(ZoneId.of(mentorProfile.getTimezone())).toLocalTime().toString())
                                .toList();

                        return MentorProfileDTO.builder()
                                .mentorId(mentorProfile.getId())
                                .name(mentorProfile.getName())
                                .phone(mentorProfile.getPhone())
                                .email(mentorProfile.getEmail())
                                .linkedinUrl(mentorProfile.getLinkedinUrl())
                                .profileUrl(mentorProfile.getProfileUrl())
                                .resumeUrl(mentorProfile.getResumeUrl())
                                .yearsOfExperience(mentorProfile.getYearsOfExperience())
                                .categories(mentorProfile.getCategories())
                                .summary(mentorProfile.getSummary())
                                .amount(mentorProfile.getAmount())
                                .terms(mentorProfile.getTerms())
                                .termsAndConditions(mentorProfile.getTermsAndConditions())
                                .timezone(mentorProfile.getTimezone())
                                .accountStatus(mentorProfile.getAccountStatus())
                                .approvalStatus(mentorProfile.getApprovalStatus())
                                .timeSlots(timeslots)
                                .build();
                    })
                    .toList();

            var adminDashboard = AdminDashboardDTO.builder()
                    .noOfMentorApproved(mentorApprovedCount)
                    .noOfMentorNotApproved(mentorNotApprovedCount)
                    .noOfMentees(noOfMentee)
                    .mentorProfileDTOS(mentorProfileDTOS)
                    .build();

            return CommonResponse.<AdminDashboardDTO>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message(ADMIN_DASHBOARD_DETAILS_SUCCESSFULLY)
                    .data(adminDashboard)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_ADMIN_DASHBOARD_DETAILS + e.getMessage());
        }

    }

    public CommonResponse<List<MentorAppointmentsDTO>> getAllMentorSessions() throws ResourceNotFoundException, UnexpectedServerException {

        try {

            List<MentorProfile> mentorProfiles = mentorProfileRepository.findAll();

            if (mentorProfiles.isEmpty()) {

                return CommonResponse.<List<MentorAppointmentsDTO>>builder()
                        .status(STATUS_FALSE)
                        .statusCode(SUCCESS_CODE)
                        .message("No mentors available")
                        .data(List.of())
                        .build();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

            Instant timeNow = Instant.now();

            List<MentorAppointmentsDTO> mentorAppointmentsDTOS = new ArrayList<>();

            for (MentorProfile mentorProfile : mentorProfiles) {


                List<Booking> bookings = bookingRepository.findByMentorIdAndPaymentStatus(mentorProfile.getId(), PaymentStatus.COMPLETED);

                List<MentorDashboardDTO> mentorDashboardDTOS = new ArrayList<>();

                for (Booking booking : bookings) {

                    String sessionTime = booking.getSessionStartTime().atZone(ZoneId.of(mentorProfile.getTimezone())).format(formatter);

                    MenteeProfile menteeProfile = menteeProfileRepository.findById(booking.getMenteeId()).orElseThrow(() -> new ResourceNotFoundException("Mentee not found with id: " + booking.getMenteeId()));

                    Instant sessionStartTime = booking.getSessionStartTime();
                    Instant sessionEndTime = sessionStartTime.plus(Duration.ofMinutes(60));

                    String status;
                    if (timeNow.isBefore(sessionStartTime)) {
                        status = "upcoming";
                    } else if (timeNow.isAfter(sessionEndTime)) {
                        status = "completed";
                    } else {
                        status = "ongoing";
                    }

                    var mentorDashboardDTO = MentorDashboardDTO.builder()
                            .sessionTime(sessionTime)
                            .sessionName(booking.getCategory())
                            .sessionDuration("1 Hr")
                            .menteeName(menteeProfile.getName())
                            .meetType(booking.getConnectMethod())
                            .status(status)
                            .mentorMeetLink(booking.getMentorMeetLink())
                            .bookingId(booking.getId())
                            .build();

                    mentorDashboardDTOS.add(mentorDashboardDTO);


                }

                var mentorAppointmentsDto = MentorAppointmentsDTO.builder()
                        .mentorId(mentorProfile.getId())
                        .mentorName(mentorProfile.getName())
                        .mentorDashboardDTOS(mentorDashboardDTOS)
                        .build();

                mentorAppointmentsDTOS.add(mentorAppointmentsDto);

            }

            return CommonResponse.<List<MentorAppointmentsDTO>>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("Loaded all mentors sessions")
                    .data(mentorAppointmentsDTOS)
                    .build();

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException("Error loading all mentors appointments: " + e.getMessage());
        }
    }

    public CommonResponse<List<MenteeAppointmentsDTO>> getAllMenteeSessions() throws ResourceNotFoundException, UnexpectedServerException {

        try {

            List<MenteeProfile> menteeProfiles = menteeProfileRepository.findAll();

            if (menteeProfiles.isEmpty()) {

                return CommonResponse.<List<MenteeAppointmentsDTO>>builder()
                        .status(STATUS_FALSE)
                        .statusCode(SUCCESS_CODE)
                        .message("No mentees available")
                        .data(List.of())
                        .build();
            }

            Instant timeNow = Instant.now();

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

            List<MenteeAppointmentsDTO> menteeAppointmentsDTOS = new ArrayList<>();

            for (MenteeProfile menteeProfile : menteeProfiles) {

                List<Booking> bookings = bookingRepository.findByMenteeIdAndPaymentStatus(menteeProfile.getId(), PaymentStatus.COMPLETED);

                List<MenteeDashboardDTO> menteeDashboardDTOS = new ArrayList<>();

                for (Booking booking : bookings) {

                    String sessionTime = booking.getSessionStartTime().atZone(ZoneId.of(menteeProfile.getTimeZone())).format(formatter);

                    MentorProfile mentorProfile = mentorProfileRepository.findById(booking.getMentorId()).orElseThrow(() -> new ResourceNotFoundException("Mentor not found with id: " + booking.getMentorId()));

                    Instant sessionStartTime = booking.getSessionStartTime();
                    Instant sessionEndTime = sessionStartTime.plus(Duration.ofMinutes(60));

                    String status;
                    if (timeNow.isBefore(sessionStartTime)) {
                        status = "upcoming";
                    } else if (timeNow.isAfter(sessionEndTime)) {
                        status = "completed";
                    } else {
                        status = "ongoing";
                    }

                    var menteeDashboardDto = MenteeDashboardDTO.builder()
                            .sessionTime(sessionTime)
                            .status(status)
                            .bookingId(booking.getId())
                            .mentorName(mentorProfile.getName())
                            .meetType(booking.getConnectMethod())
                            .bookingId(booking.getId())
                            .build();

                    menteeDashboardDTOS.add(menteeDashboardDto);
                }

                var menteeAppointmentsDto = MenteeAppointmentsDTO.builder()
                        .menteeId(menteeProfile.getId())
                        .menteeName(menteeProfile.getName())
                        .menteeDashboardDTOS(menteeDashboardDTOS)
                        .build();

                menteeAppointmentsDTOS.add(menteeAppointmentsDto);

            }

            return CommonResponse.<List<MenteeAppointmentsDTO>>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("Loaded all mentees sessions")
                    .data(menteeAppointmentsDTOS)
                    .build();

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException("Error while loading all mentees sessions: " + e.getMessage());
        }


    }

    public CommonResponse<List<MentorOverviewDTO>> getMentorsOverview() throws UnexpectedServerException {

        try {

            List<MentorProfile> mentorProfiles = mentorProfileRepository.findAll();

            if (mentorProfiles.isEmpty()) {

                return CommonResponse.<List<MentorOverviewDTO>>builder()
                        .status(STATUS_FALSE)
                        .statusCode(SUCCESS_CODE)
                        .message("No mentors available")
                        .data(List.of())
                        .build();
            }

            Instant timeNow = Instant.now();

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

            List<MentorOverviewDTO> mentorOverviewDTOS = mentorProfiles.stream()
                    .map(mentorProfile -> {

                        List<Booking> bookings = bookingRepository.findByMentorIdAndPaymentStatus(mentorProfile.getId(), PaymentStatus.COMPLETED);

                        // filter must return a boolean true or false
                        Long futureSessions = bookings.stream()
                                .filter(booking -> timeNow.isBefore(booking.getSessionStartTime()))
                                .count();

                        Long completedSessions = bookings.stream()
                                .filter(booking -> {

                                    Instant sessionStartTime = booking.getSessionStartTime();
                                    Instant sessionEndTime = sessionStartTime.plus(Duration.ofMinutes(60));

                                    return timeNow.isAfter(sessionEndTime);
                                })
                                .count();

                        return MentorOverviewDTO.builder()
                                .mentorId(mentorProfile.getId())
                                .mentorName(mentorProfile.getName())
                                .joinDate(mentorProfile.getCreatedAt().atZone(ZoneId.of(mentorProfile.getTimezone())).format(formatter))
                                .futureSessions(futureSessions)
                                .completedSessions(completedSessions)
                                .accountStatus(mentorProfile.getAccountStatus())
                                .profileUrl(mentorProfile.getProfileUrl())
                                .build();


                    })
                    .toList();

            return CommonResponse.<List<MentorOverviewDTO>>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("Loaded all mentors details")
                    .data(mentorOverviewDTOS)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException("Error while loading mentors details: " + e.getMessage());
        }


    }

    public CommonResponse<List<MenteeOverviewDTO>> getMenteesOverview() throws UnexpectedServerException {

        try {

            List<MenteeProfile> menteeProfiles = menteeProfileRepository.findAll();

            if (menteeProfiles.isEmpty()) {

                return CommonResponse.<List<MenteeOverviewDTO>>builder()
                        .status(STATUS_FALSE)
                        .statusCode(SUCCESS_CODE)
                        .message("No mentees available")
                        .data(List.of())
                        .build();
            }

            Instant timeNow = Instant.now();

            var formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

            List<MenteeOverviewDTO> menteeOverviewDTOS = menteeProfiles.stream()
                    .map(menteeProfile -> {

                        List<Booking> bookings = bookingRepository.findByMenteeIdAndPaymentStatus(menteeProfile.getId(), PaymentStatus.COMPLETED);

                        // filter must return a boolean true or false
                        Long futureSessions = bookings.stream()
                                .filter(booking -> timeNow.isBefore(booking.getSessionStartTime()))
                                .count();

                        Long completedSessions = bookings.stream()
                                .filter(booking -> {

                                    Instant sessionStartTime = booking.getSessionStartTime();
                                    Instant sessionEndTime = sessionStartTime.plus(Duration.ofMinutes(60));

                                    return timeNow.isAfter(sessionEndTime);
                                })
                                .count();

                        return MenteeOverviewDTO.builder()
                                .menteeId(menteeProfile.getId())
                                .menteeName(menteeProfile.getName())
                                .joinDate(menteeProfile.getCreatedAt().atZone(ZoneId.of(menteeProfile.getTimeZone())).format(formatter))
                                .futureSessions(futureSessions)
                                .completedSessions(completedSessions)
                                .accountStatus(AccountStatus.ACTIVE)
                                .profileUrl(menteeProfile.getProfileUrl())
                                .build();


                    })
                    .toList();

            return CommonResponse.<List<MenteeOverviewDTO>>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("Loaded all mentees details")
                    .data(menteeOverviewDTOS)
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException("Error while loading mentees details: " + e.getMessage());
        }

    }

    public CommonResponse<AdminDashboardDTO> updateMentorApprovalStatus(Long mentorId,ApprovalRequestDTO request) throws ResourceNotFoundException, UnexpectedServerException {


        try {

            MentorProfile mentor = mentorProfileRepository.findById(mentorId).orElseThrow(() -> new ResourceNotFoundException("Mentor not found with id: " + mentorId));

            String action = request.getStatus();

            if ("APPROVED".equalsIgnoreCase(action)) {

                mentor.setApprovalStatus(ApprovalStatus.ACCEPTED);
                mentor.setAccountStatus(AccountStatus.ACTIVE);

            } else if ("REJECTED".equalsIgnoreCase(action)) {

                mentor.setApprovalStatus(ApprovalStatus.REJECTED);
                mentor.setAccountStatus(AccountStatus.INACTIVE);

            } else {
                throw new InvalidFieldValueException("Action must be either approved or rejected");
            }

            mentorProfileRepository.save(mentor);

            return CommonResponse.<AdminDashboardDTO>builder()
                    .status(STATUS_TRUE)
                    .message("Mentor " + action + " successfully")
                    .statusCode(200)
                    .data(getAdminDashboardDetails().getData())
                    .build();

        }catch (ResourceNotFoundException | InvalidFieldValueException e){
            throw e;
        }catch (Exception e){
            throw new UnexpectedServerException("Error while approving or rejecting the mentor: " + e.getMessage());
        }
    }

}