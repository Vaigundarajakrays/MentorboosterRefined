package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.TimeSlotDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.FixedTimeSlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mentorboosters/api")
public class FixedTimeSlotController {

    private final FixedTimeSlotService fixedTimeSlotService;

    public FixedTimeSlotController(FixedTimeSlotService fixedTimeSlotService){this.fixedTimeSlotService=fixedTimeSlotService;}

    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) means We are telling spring to convert the String into LocalDate with ISO format yyyy-mm-dd
    @GetMapping("/getAllSlotsForMentor/{mentorId}")
    public CommonResponse<List<TimeSlotDTO>> getAllTimeSlotsOfMentor(@PathVariable Long mentorId,
                                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                     @RequestParam String timezone) throws UnexpectedServerException, ResourceNotFoundException {
        return fixedTimeSlotService.getAllTimeSlotsOfMentor(mentorId, date, timezone);
    }
}
