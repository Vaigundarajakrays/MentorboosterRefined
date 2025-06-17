package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.AdminDashboardDTO;
import com.mentorboosters.app.dto.MentorAppointmentsDTO;
import com.mentorboosters.app.dto.MentorDashboardDTO;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/getDashboardDetails")
    public CommonResponse<AdminDashboardDTO> getAdminDashboardDetails() throws UnexpectedServerException {
        return adminService.getAdminDashboardDetails();
    }

//    @GetMapping("/getAllMentorSessions")
//    public CommonResponse<List<MentorAppointmentsDTO>> getAllMentorSessions(){
//        return adminService.getAllMentorSessions();
//    }


}
