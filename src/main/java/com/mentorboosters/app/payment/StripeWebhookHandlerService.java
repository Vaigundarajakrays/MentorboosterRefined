package com.mentorboosters.app.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentorboosters.app.enumUtil.PaymentStatus;
import com.mentorboosters.app.enumUtil.ZoomContextType;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.repository.BookingRepository;
import com.mentorboosters.app.zoom.ZoomMeetingResponse;
import com.mentorboosters.app.zoom.ZoomMeetingService;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookHandlerService {

    private final ObjectMapper objectMapper;
    private final BookingRepository bookingRepository;
    private final ZoomMeetingService zoomMeetingService;

    @Async
    public void handleSessionEventAsync(Event event, PaymentStatus statusToSet) {
        try {
            JsonNode data = objectMapper.readTree(event.getData().getObject().toJson());
            String sessionId = data.get("id").asText();
            JsonNode metadata = data.get("metadata");

            if(metadata==null){
                log.warn("Metadata should not be null");
                return;
            }

            String bookingIdStr = metadata.has("bookingId")
                    ? metadata.get("bookingId").asText()
                    : null;

            Booking booking = bookingRepository.findByStripeSessionId(sessionId);

            // Don't throw custom exception in this controller because this api called by stripe not FRONTEND
            if (booking == null) {
                log.warn("Booking not found for sessionId: {}", sessionId);
                return;
            }

            // Set status
            booking.setPaymentStatus(statusToSet);

            if (statusToSet.isCompleted()) {

                String mentorEmail = metadata.get("mentorEmail").asText();
                String menteeEmail = metadata.get("menteeEmail").asText();

                String mentorName = metadata.get("mentorName").asText();
                String menteeName = metadata.get("menteeName").asText();

                Instant sessionStart = Instant.parse(metadata.get("sessionStart").asText());
                Instant sessionEnd = Instant.parse(metadata.get("sessionEnd").asText());

                String mentorTimezone = metadata.get("mentorTimezone").asText();
                String menteeTimezone = metadata.get("menteeTimezone").asText();

                // Payment intent id is used for refund,
                String paymentIntentId = data.has("payment_intent") && !data.get("payment_intent").isNull() ? data.get("payment_intent").asText() : null;
                booking.setStripePaymentIntentId(paymentIntentId);

                // Create Zoom meeting and get links
                ZoomMeetingResponse zoomLinks = zoomMeetingService
                        .createZoomMeetingAndNotify(mentorEmail, menteeEmail,mentorName, menteeName, sessionStart, sessionEnd, null, ZoomContextType.NEW, mentorTimezone, menteeTimezone);

                booking.setMentorMeetLink(zoomLinks.getStartUrl());
                booking.setUserMeetLink(zoomLinks.getJoinUrl());
                log.info("Zoom meeting created for booking ID {} - mentor: {}, user: {}", bookingIdStr, mentorEmail, menteeEmail);

            }

            bookingRepository.save(booking);
            log.info("Booking updated with payment status: {}", statusToSet);

        } catch (Exception e) {
            log.error("Error updating booking payment status: {}", e.getMessage(), e);
        }
    }
}
