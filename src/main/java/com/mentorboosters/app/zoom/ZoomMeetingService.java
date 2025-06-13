package com.mentorboosters.app.zoom;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
public class ZoomMeetingService {

    @Autowired
    private ZoomTokenService zoomTokenService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from}")
    private String mailFrom;


    private final ObjectMapper objectMapper = new ObjectMapper();

    public ZoomMeetingResponse createZoomMeetingAndNotify(String mentorEmail, String userEmail, Instant startTime, Instant endTime) throws Exception {
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


            String mentorContent = "Start URL: " + startUrl + "\n\nAdd to Calendar:\n" + mentorCalendarLink;
            String userContent = "Join URL: " + joinUrl + "\n\nAdd to Calendar:\n" + userCalendarLink;

            sendEmail(mentorEmail, "Zoom Meeting Scheduled", mentorContent);
            sendEmail(userEmail, "Zoom Meeting Scheduled", userContent);

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

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(mailFrom);
        mailSender.send(message);
    }
}
