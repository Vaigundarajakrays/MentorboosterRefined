package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.*;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController{

    private final AdminService adminService;

    @GetMapping("/getDashboardDetails")
    public CommonResponse<AdminDashboardDTO> getAdminDashboardDetails() throws UnexpectedServerException {
        return adminService.getAdminDashboardDetails();
    }

    @GetMapping("/getAllMentorSessions")
    public CommonResponse<List<MentorAppointmentsDTO>> getAllMentorSessions() throws UnexpectedServerException, ResourceNotFoundException {
        return adminService.getAllMentorSessions();
    }

    @GetMapping("/getAllMenteeSessions")
    public CommonResponse<List<MenteeAppointmentsDTO>> getAllMenteeSessions() throws UnexpectedServerException, ResourceNotFoundException {
        return adminService.getAllMenteeSessions();
    }

    @GetMapping("/mentors/overview")
    public CommonResponse<List<MentorOverviewDTO>> getMentorsOverview() throws UnexpectedServerException {
        return adminService.getMentorsOverview();
    }

    @GetMapping("/mentees/overview")
    public CommonResponse<List<MenteeOverviewDTO>> getMenteesOverview() throws UnexpectedServerException {
        return adminService.getMenteesOverview();
    }

    @PatchMapping("/mentors/{mentorId}/approval-status")
    public CommonResponse<ApprovalRequestDTO> updateMentorStatus(@PathVariable Long mentorId,@RequestBody ApprovalRequestDTO request) throws UnexpectedServerException, ResourceNotFoundException {
        return adminService.updateMentorApprovalStatus(mentorId, request);

    }
}

