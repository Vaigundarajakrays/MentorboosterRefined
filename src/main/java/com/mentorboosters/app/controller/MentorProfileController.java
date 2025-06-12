package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.MentorProfileDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.MentorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class MentorProfileController {

    private final MentorProfileService mentorNewService;

    @PostMapping("/register")
    public CommonResponse<MentorProfile> registerNewMentor(@RequestBody MentorProfileDTO mentorNewDTO) throws UnexpectedServerException {
        return mentorNewService.registerMentor(mentorNewDTO);
    }

    @GetMapping("/getProfileDetails/{id}")
    public CommonResponse<MentorProfileDTO> getProfileDetails(@PathVariable Long id) throws UnexpectedServerException, ResourceNotFoundException {
        return mentorNewService.getProfileDetails(id);
    }

    @PatchMapping("/updateProfile/{id}")
    public CommonResponse<MentorProfileDTO> updateProfile(@PathVariable Long id, @RequestBody MentorProfileDTO mentorProfileDTO) throws UnexpectedServerException, ResourceNotFoundException {
        return mentorNewService.updateMentorProfile(id, mentorProfileDTO);
    }



}
