package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.AdminDashboardDTO;
import com.mentorboosters.app.dto.MentorAppointmentsDTO;
import com.mentorboosters.app.dto.MentorDashboardDTO;
import com.mentorboosters.app.dto.MentorProfileDTO;
import com.mentorboosters.app.enumUtil.ApprovalStatus;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.repository.BookingRepository;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;
    private final BookingRepository bookingRepository;

    public CommonResponse<AdminDashboardDTO> getAdminDashboardDetails() throws UnexpectedServerException {

        try {

            Long mentorApprovedCount = mentorProfileRepository.countByApprovalStatus(ApprovalStatus.ACCEPTED);
            Long mentorNotApprovedCount = mentorProfileRepository.countByApprovalStatus(ApprovalStatus.PENDING);

            Long noOfMentee = menteeProfileRepository.count();

            // Can use findAllByApprovalStatus too, both works
            List<MentorProfile> mentorProfiles = mentorProfileRepository.findByApprovalStatus(ApprovalStatus.PENDING);

            if(mentorProfiles.isEmpty()){

                var adminDashboard = AdminDashboardDTO.builder()
                        .noOfMentorApproved(mentorApprovedCount)
                        .noOfMentorNotApproved(mentorNotApprovedCount)
                        .noOfMentees(noOfMentee)
                        .mentorProfileDTOS(List.of())
                        .build();

                return CommonResponse.<AdminDashboardDTO>builder()
                        .status(STATUS_FALSE)
                        .statusCode(SUCCESS_CODE)
                        .message("No mentors found")
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
                    .message("Loaded admin dashboard details successfully")
                    .data(adminDashboard)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException("Error while loading admin dashboard details: " + e.getMessage());
        }

    }

//    public CommonResponse<List<MentorAppointmentsDTO>> getAllMentorSessions() throws ResourceNotFoundException {
//
//        List<MentorProfile> mentorProfiles = mentorProfileRepository.findAll();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
//
//        Instant timeNow = Instant.now();
//
//        List<MentorAppointmentsDTO> mentorAppointmentsDTOS = new ArrayList<>();
//
//        for(MentorProfile mentorProfile: mentorProfiles){
//
//
//            List<Booking> bookings = bookingRepository.findByMentorIdAndPaymentStatus(mentorProfile.getId(), "completed");
//
//            List<MentorDashboardDTO> mentorDashboardDTOS = new ArrayList<>();
//
//            for(Booking booking: bookings){
//
//                String sessionTime = booking.getSessionStartTime().atZone(ZoneId.of(mentorProfile.getTimezone())).format(formatter);
//
//                MenteeProfile menteeProfile = menteeProfileRepository.findById(booking.getMenteeId()).orElseThrow(() -> new ResourceNotFoundException("Mentee not found with id: " + booking.getMenteeId()));
//
//                Instant sessionStartTime = booking.getSessionStartTime();
//                Instant sessionEndTime = sessionStartTime.plus(Duration.ofMinutes(60));
//
//                String status;
//                if(timeNow.isBefore(sessionStartTime)){
//                    status= "Upcoming";
//                } else if(timeNow.isAfter(sessionEndTime)){
//                    status="Completed";
//                } else{
//                    status="Ongoing";
//                }
//
//                var mentorDashboardDTO = MentorDashboardDTO.builder()
//                        .sessionTime(sessionTime)
//                        .sessionName(booking.getCategory())
//                        .sessionDuration("1 Hr")
//                        .menteeName(menteeProfile.getName())
//                        .meetType(booking.getConnectMethod())
//                        .status(status)
//                        .mentorMeetLink(booking.getMentorMeetLink())
//                        .build();
//
//                mentorDashboardDTOS.add(mentorDashboardDTO);
//
//
//            }
//
//            var mentorAppointmentsDto = MentorAppointmentsDTO.bu
//
//
//        }
//    }
}
