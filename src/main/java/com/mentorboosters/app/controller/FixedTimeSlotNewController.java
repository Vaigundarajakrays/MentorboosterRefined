package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.TimeSlotDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.FixedTimeSlotNewService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FixedTimeSlotNewController {

    private final FixedTimeSlotNewService fixedTimeSlotNewService;

    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) means We are telling spring to convert the String into LocalDate with ISO format yyyy-mm-dd
    @GetMapping("/getTimeSlotsForMentor")
    public CommonResponse<List<TimeSlotDTO>> getAllTimeSlotsOfMentor(
            @RequestParam Long mentorId,
            @RequestParam Long menteeId,
            @RequestParam String date) throws UnexpectedServerException, ResourceNotFoundException {
        return fixedTimeSlotNewService.getTimeSlotsOfMentor(mentorId, menteeId, date);
    }
}
