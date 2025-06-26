package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.AllMentorsResponseDTO;
import com.mentorboosters.app.dto.MenteeDashboardDTO;
import com.mentorboosters.app.dto.MentorDashboardDTO;
import com.mentorboosters.app.dto.MentorProfileDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.MentorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class MentorProfileController {

    private final MentorProfileService mentorNewService;

    @PostMapping("/register")
    public CommonResponse<String> registerNewMentor(@RequestBody MentorProfileDTO mentorNewDTO) throws UnexpectedServerException {
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

    @GetMapping("/getAppointments/{mentorId}")
    public CommonResponse<List<MentorDashboardDTO>> getAppointments(@PathVariable Long mentorId) throws ResourceNotFoundException, UnexpectedServerException {
        return mentorNewService.getAppointments(mentorId);
    }

//    @GetMapping("/search")
//    public CommonResponse<List<MentorProfileDTO>> getMentorsByCategoryName(@RequestParam String category) {
//        return mentorNewService.getMentorsByCategoryName(category);
//    }

    // It returns only approved mentors, other apis may not, clarify
    @GetMapping("/getAllMentors")
    public CommonResponse<List<AllMentorsResponseDTO>> getAllMentors() throws UnexpectedServerException {
        return mentorNewService.getAllMentors();
    }


}
