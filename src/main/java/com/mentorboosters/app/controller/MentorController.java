package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.*;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Mentor;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MentorController {

    private final MentorService mentorService;

    @Autowired
    public MentorController(MentorService mentorService){this.mentorService=mentorService;}

    @GetMapping("/getAllMentorsWithSlots")
    public CommonResponse<List<Mentor>> findAllMentorsWithSlots() throws UnexpectedServerException {return mentorService.findAllMentorsWithSlots();}

//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PostMapping("/saveMentor")
//    public CommonResponse<Mentor> saveMentorDetails(@RequestBody Mentor mentor) throws UnexpectedServerException {
//        return mentorService.saveMentor(mentor);
//    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/updateMentor/{id}")
    public CommonResponse<Mentor> updateMentorDetails(@PathVariable Long id, @RequestBody Mentor mentor) throws ResourceNotFoundException, UnexpectedServerException {
        return mentorService.updateMentor(id, mentor);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteMentor/{id}")
    public CommonResponse<Mentor> deleteMentorDetails(@PathVariable Long id) throws ResourceNotFoundException, UnexpectedServerException {
        return mentorService.deleteMentor(id);
    }

    @GetMapping("/getVerifiedMentors")
    public CommonResponse<List<VerifiedMentorDTO>> getVerifiedMentors() throws UnexpectedServerException {
        return mentorService.getVerifiedMentors();
    }

    @GetMapping("/getTopRatedMentors")
    public CommonResponse<List<TopRatedMentorsDTO>> getTopRatedMentors() throws UnexpectedServerException {
        return mentorService.getTopRatedMentors();
    }

    @GetMapping("/getTopMentors")
    public CommonResponse<List<TopMentorsDTO>> getTopMentorsDetails() throws UnexpectedServerException {
        return mentorService.getTopMentors();
    }

    @GetMapping("/searchMentors")
    public CommonResponse<List<SearchMentorsDTO>> searchMentors(@RequestParam String keyword) throws UnexpectedServerException {
        return mentorService.searchMentors(keyword);
    }
    
}
