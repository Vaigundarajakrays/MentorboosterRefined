package com.mentorboosters.app.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.repository.BookingRepository;
import com.mentorboosters.app.zoom.ZoomMeetingResponse;
import com.mentorboosters.app.zoom.ZoomMeetingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/mentorboosters/api")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final ObjectMapper objectMapper;
    private final BookingRepository bookingRepository;
    private final ZoomMeetingService zoomMeetingService;

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    public StripeWebhookController(ObjectMapper objectMapper,
                                   BookingRepository bookingRepository,
                                   ZoomMeetingService zoomMeetingService) {
        this.objectMapper = objectMapper;
        this.bookingRepository = bookingRepository;
        this.zoomMeetingService = zoomMeetingService;
    }

    @PostMapping("/stripe/webhook")
    @Transactional
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            logger.warn("⚠️ Invalid Stripe signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Stripe signature");
        } catch (Exception e) {
            logger.error("Webhook error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }

        String eventType = event.getType();
        logger.info("Received Stripe event: {}", eventType);

        switch (eventType) {
            case "checkout.session.completed":
            case "checkout.session.async_payment_succeeded":
                handleSessionEvent(event, "completed");
                break;

            case "checkout.session.expired":
                handleSessionEvent(event, "expired");
                break;

            case "checkout.session.async_payment_failed":
                handleSessionEvent(event, "failure");
                break;

            default:
                logger.warn("Unhandled Stripe event type: {}", eventType);
                break;
        }

        return ResponseEntity.ok("✅ Webhook processed: " + eventType);
    }

    private void handleSessionEvent(Event event, String statusToSet) {
        try {
            JsonNode data = objectMapper.readTree(event.getData().getObject().toJson());
            String sessionId = data.get("id").asText();
            JsonNode metadata = data.get("metadata");

            String bookingIdStr = metadata != null && metadata.has("bookingId")
                    ? metadata.get("bookingId").asText()
                    : null;

            Booking booking = bookingRepository.findByStripeSessionId(sessionId);

            // Don't throw custom exception in this controller because this api called by stripe not FRONTEND
            if (booking == null) {
                logger.warn("Booking not found for sessionId: {}", sessionId);
                return;
            }

            // Set status
            booking.setPaymentStatus(statusToSet);


            String mentorEmail = metadata.get("mentorEmail").asText();

            String userEmail = metadata.get("userEmail").asText();

            String startTimeStr = metadata.get("startTime").asText();

            String endTimeStr = metadata.get("endTime").asText();

            // Convert start and end time back to java.util.Date
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            Date startTime = sdf.parse(startTimeStr);
            Date endTime = sdf.parse(endTimeStr);

            if ("completed".equalsIgnoreCase(statusToSet)) {

                // Create Zoom meeting and get links
                ZoomMeetingResponse zoomLinks = zoomMeetingService
                        .createZoomMeetingAndNotify(mentorEmail, userEmail, startTime, endTime);

                booking.setMentorMeetLink(zoomLinks.getStartUrl());
                booking.setUserMeetLink(zoomLinks.getJoinUrl());
                logger.info("Zoom meeting created for booking ID {} - mentor: {}, user: {}", bookingIdStr, mentorEmail, userEmail);

            }

            bookingRepository.save(booking);
            logger.info("Booking updated with payment status: {}", statusToSet);

        } catch (Exception e) {
            logger.error("Error updating booking payment status: {}", e.getMessage(), e);
        }
    }
}



