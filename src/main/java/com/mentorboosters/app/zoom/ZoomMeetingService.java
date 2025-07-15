package com.mentorboosters.app.zoom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentorboosters.app.enumUtil.ZoomContextType;
import com.mentorboosters.app.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class ZoomMeetingService {

    private final ZoomTokenService zoomTokenService;
    private final JavaMailSender mailSender;
    private final EmailService emailService;

    @Value("${mail.from}")
    private String mailFrom;


    private final ObjectMapper objectMapper = new ObjectMapper();

    public ZoomMeetingResponse createZoomMeetingAndNotify(String mentorEmail, String menteeEmail, String mentorName, String menteeName, Instant startTime, Instant endTime, Instant oldStart, ZoomContextType contextType, String mentorTimezone, String menteeTimezone) throws Exception {
        String accessToken = zoomTokenService.getAccessToken();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Zoom expects start time to be in ISO-8601 format: 2025-11-06T04:00:00Z. So instant.toString is crt.
        Map<String, Object> meetingDetails = new HashMap<>();
        meetingDetails.put("topic", "MentorBooster Session");
        meetingDetails.put("type", 2); // Scheduled
        meetingDetails.put("start_time", startTime.toString());
        meetingDetails.put("duration", 60);
        meetingDetails.put("timezone", "UTC");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(meetingDetails, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.zoom.us/v2/users/me/meetings", request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
            String startUrl = (String) responseBody.get("start_url");
            String joinUrl = (String) responseBody.get("join_url");

            String mentorCalendarLink = generateGoogleCalendarLink(
                    "MentorBooster Session",
                    "Zoom meeting you're hosting.",
                    startUrl, // <-- host link here
                    startTime,
                    endTime
            );

            String userCalendarLink = generateGoogleCalendarLink(
                    "MentorBooster Session",
                    "Zoom meeting with your mentor.",
                    joinUrl, // <-- public attendee link
                    startTime,
                    endTime
            );

            ZoneId mentorTimeZone = ZoneId.of(mentorTimezone);
            ZoneId menteeTimeZone = ZoneId.of(menteeTimezone);

            // Date time for mentor
            String oldSessionDateTimeForMentor = oldStart != null ? formattedDateTime(oldStart, mentorTimeZone) : null;
            String newSessionDateForMentor = formattedDate(startTime, mentorTimeZone);
            String newSessionTimeForMentor = formattedTime(startTime, mentorTimeZone);

            //Date Time for mentee
            String oldSessionDateTimeForMentee = oldStart != null ? formattedDateTime(oldStart, menteeTimeZone) : null;
            String newSessionDateForMentee = formattedDate(startTime, menteeTimeZone);
            String newSessionTimeForMentee = formattedTime(startTime, menteeTimeZone);

            // Prepare email content
            String menteeSubject;
            String menteeBody;
            String mentorSubject;
            String mentorBody;

            if(contextType.isReschedule() && oldStart != null){

                menteeSubject = "Your MentorBooster Session Has Been Rescheduled";
                mentorSubject = "Your MentorBooster Session Has Been Rescheduled";

                menteeBody = String.format("""
                Hi %s,

                Your previously scheduled session with %s on %s has been rescheduled.

                🆕 New Schedule:
                - 📅 Date: %s
                - 🕒 Time: %s (%s)
                - 📍 Location: Zoom

                🔗 Join Zoom Meeting:
                %s

                🗓️ Add to Calendar:
                %s

                If you have any questions or need assistance, feel free to reply to this email.

                Thank you,
                MentorBooster Team
                """,
                        menteeName,
                        mentorName,
                        oldSessionDateTimeForMentee,
                        newSessionDateForMentee,
                        newSessionTimeForMentee,
                        menteeTimezone,
                        joinUrl,
                        userCalendarLink
                );

                mentorBody = String.format("""
                Hi %s,

                The session with %s originally scheduled for %s has been rescheduled.

                🆕 New Schedule:
                - 📅 Date: %s
                - 🕒 Time: %s (%s)
                - 📍 Location: Zoom

                🔗 Start Zoom Meeting:
                %s

                🗓️ Add to Calendar:
                %s

                Please be sure to start the meeting on time.

                Best regards,
                MentorBooster Team
                """,
                        mentorName,
                        menteeName,
                        oldSessionDateTimeForMentor,
                        newSessionDateForMentor,
                        newSessionTimeForMentor,
                        mentorTimezone,
                        startUrl,
                        mentorCalendarLink
                );

            } else{

                menteeSubject = "Your MentorBooster Zoom Session is Scheduled";
                mentorSubject = "Your MentorBooster Zoom Session is Scheduled";

                menteeBody = String.format("""
                Hi %s,

                Your Zoom session with %s has been scheduled.

                🗓️ Date: %s
                🕒 Time: %s (%s)
                📍 Location: Zoom

                🔗 Join Zoom Meeting:
                %s

                🗓️ Add to Calendar:
                %s

                See you soon!

                MentorBooster Team
                """,
                        menteeName,
                        mentorName,
                        newSessionDateForMentee,
                        newSessionTimeForMentee,
                        menteeTimezone,
                        joinUrl,
                        userCalendarLink
                );

                mentorBody = String.format("""
                Hi %s,

                You have a new session with %s scheduled.

                🗓️ Date: %s
                🕒 Time: %s (%s)
                📍 Location: Zoom

                🔗 Start Zoom Meeting:
                %s

                🗓️ Add to Calendar:
                %s

                Best,
                MentorBooster Team
                """,
                        mentorName,
                        menteeName,
                        newSessionDateForMentor,
                        newSessionTimeForMentor,
                        mentorTimezone,
                        startUrl,
                        mentorCalendarLink
                );
            }

            emailService.sendEmail(mentorEmail, mentorSubject, mentorBody);
            emailService.sendEmail(menteeEmail, menteeSubject, menteeBody);

            return ZoomMeetingResponse.builder()
                    .startUrl(startUrl)
                    .joinUrl(joinUrl)
                    .build();
        } else {
            throw new Exception("Failed to create Zoom meeting.");
        }
    }

    private String generateGoogleCalendarLink(String title, String details, String joinUrl, Instant startTime, Instant endTime) {

        // 🔬 Why Instant.toString() fails here?
        // Because it returns: 2025-11-06T04:00:00Z
        // But Google wants: 20251106T040000Z
        String startStr = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
                .withZone(ZoneOffset.UTC)
                .format(startTime);

        String endStr = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
                .withZone(ZoneOffset.UTC)
                .format(endTime);


        return String.format(
                "https://calendar.google.com/calendar/r/eventedit?text=%s&details=%s%%0AJoin+Here:%%20%s&location=Zoom&dates=%s/%s",
                encode(title),
                encode(details),
                encode(joinUrl),
                startStr,
                endStr
        );
    }




    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }



    private String formattedDateTime(Instant instant, ZoneId timezone) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(timezone).format(instant);
    }



    private String formattedDate(Instant instant, ZoneId timezone){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(timezone).format(instant);
    }



    private String formattedTime(Instant instant, ZoneId timezone){
        return DateTimeFormatter.ofPattern("hh:mm a").withZone(timezone).format(instant);
    }

}
