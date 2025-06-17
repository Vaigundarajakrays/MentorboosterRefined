package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.MenteeDashboardDTO;
import com.mentorboosters.app.dto.MenteeProfileDTO;
import com.mentorboosters.app.dto.MentorProfileDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.MenteeProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentee")
@RequiredArgsConstructor
public class MenteeProfileController {

    private final MenteeProfileService menteeProfileService;

    @PostMapping("/register")
    public CommonResponse<MenteeProfile> signUp(@RequestBody MenteeProfileDTO menteeProfileDTO) throws UnexpectedServerException {
        return menteeProfileService.registerMentee(menteeProfileDTO);
    }

    @GetMapping("/getProfileDetails/{id}")
    public CommonResponse<MenteeProfileDTO> getProfileDetails(@PathVariable Long id) throws UnexpectedServerException, ResourceNotFoundException {
        return menteeProfileService.getMenteeProfile(id);
    }

    @PatchMapping("/updateProfile/{id}")
    public CommonResponse<MenteeProfileDTO> updateProfile(@PathVariable Long id, @RequestBody MenteeProfileDTO menteeProfileDTO) throws UnexpectedServerException, ResourceNotFoundException {
        return menteeProfileService.updateMenteeProfile(id, menteeProfileDTO);
    }

    @GetMapping("/getAppointments/{menteeId}")
    public CommonResponse<List<MenteeDashboardDTO>> getAppointments(@PathVariable Long menteeId) throws ResourceNotFoundException, UnexpectedServerException {
        return menteeProfileService.getAppointments(menteeId);
    }
}
