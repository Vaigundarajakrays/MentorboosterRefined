package com.mentorboosters.app.gmeetservicebased;

import com.google.api.services.calendar.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/calendar")
public class GoogleCalendarController {

    @Autowired
    private GoogleServiceAccountConfig calendarConfig;

    @Autowired
    private GoogleCalendarService calendarService;

    @GetMapping("/create-meet")
    public String createMeet() {
        try {
            Calendar calendar = calendarConfig.getCalendarService();

            // Just for test — in real use from booking
            String mentorEmail = "jvrchingam@gmail.com";
            String userEmail = "jvraja2001@gmail.com";
            Date start = new Date(System.currentTimeMillis() + 3600000); // +1 hr
            Date end = new Date(System.currentTimeMillis() + 7200000);   // +2 hr

            String meetLink = calendarService.createMeetEvent(calendar, mentorEmail, userEmail, start, end);

            return "Meet created successfully: " + meetLink;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
