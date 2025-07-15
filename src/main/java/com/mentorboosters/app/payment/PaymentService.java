package com.mentorboosters.app.payment;

import com.mentorboosters.app.dto.BookingDTO;
import com.mentorboosters.app.enumUtil.PaymentStatus;
import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.model.FixedTimeSlotNew;
import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.MentorProfile;
import com.mentorboosters.app.repository.*;
import com.mentorboosters.app.response.CommonResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    private final BookingRepository bookingRepository;
    private final FixedTimeSlotNewRepository fixedTimeSlotNewRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    // set the api key globally, if used refund method in a separate file and mark it as @Component no need to once again set apiKey in that file.
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    // Make the amount to double not long, because if UI send 2.5 it will cause error
    // Change like this Math.round(request.getAmount() * 100)
    // If math.round is not used, java convert 2.5*100 as 249.999
    // Stripe expects amount to be long or integer not float or double
    // Math.round convert the double to long automatically
    public CommonResponse<PaymentResponse> checkoutProducts(BookingDTO bookingDTO) throws StripeException, UnexpectedServerException, ResourceNotFoundException {

        try {

            FixedTimeSlotNew currentSlot = fixedTimeSlotNewRepository.findById(bookingDTO.getTimeSlotId()).orElseThrow(() -> new ResourceNotFoundException(TIMESLOT_NOT_FOUND_WITH_ID + bookingDTO.getTimeSlotId()));

            MenteeProfile menteeProfile = menteeProfileRepository.findById(bookingDTO.getMenteeId()).orElseThrow(() -> new ResourceNotFoundException("Mentee not found with ID " + bookingDTO.getMenteeId()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate bookingDate;

            try {
                bookingDate = LocalDate.parse(bookingDTO.getBookingDate(), formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidFieldValueException("Invalid booking date format. Expected yyyy-MM-dd.");
            }

            final ZoneId zoneId;
            try {
                zoneId = ZoneId.of(menteeProfile.getTimeZone());
            } catch (DateTimeException e){
                throw new InvalidFieldValueException("Invalid time zone");
            }

            // Converting the booked date to utc
            LocalDate date = bookingDate;                               // 2025-11-06
            ZonedDateTime startOfDayZoned = date.atStartOfDay(zoneId);  // 2025-11-06T00:00+05:30[Asia/Kolkata]
            Instant bookedUtcDate = startOfDayZoned.toInstant();        // 2025-11-05T18:30:00Z

            // Converting current slot in UTC to current slot in mentee time zone with date
            LocalTime currentSlotTime = currentSlot.getTimeStart()
                    .atZone(ZoneOffset.UTC)         // this line not needed. It says java that treat the current slot as UTC. but we already storing it as UTC. i think this line is redundant.
                    .withZoneSameInstant(zoneId)
                    .toLocalTime();
            ZonedDateTime currentSlotStartDateTime = bookingDate.atTime(currentSlotTime).atZone(zoneId);
            ZonedDateTime currentSlotEndDateTime = currentSlotStartDateTime.plusHours(1);

            Instant sessionStart = currentSlotStartDateTime.toInstant();

            // For DB CHECKING
            ZonedDateTime dayStartZoned = date.atStartOfDay(zoneId);
            ZonedDateTime dayEndZoned = dayStartZoned.plusDays(1);
            Instant utcStart = dayStartZoned.toInstant();
            Instant utcEnd = dayEndZoned.toInstant();

            // Checking if any other user already booked this slot
            // 2 0r more users may hit api at same time, so more users have same id, same sessionStartTime, samePaymentStatus
            // Also, we have BookingCleanUpService, which will for every 5 min, convert HOLD to EXPIRED, if HOLD is before the (currentTime - 15 min)
            List<Booking> alreadyBooked = bookingRepository.findByTimeSlotIdAndSessionStartTimeAndPaymentStatusIn(bookingDTO.getTimeSlotId(), sessionStart, List.of(PaymentStatus.COMPLETED, PaymentStatus.HOLD));
            if(!alreadyBooked.isEmpty()){

                for(Booking booking : alreadyBooked){

                    if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
                        throw new ResourceAlreadyExistsException("Slot is already booked.");
                    }

                    if (booking.getPaymentStatus() == PaymentStatus.HOLD && booking.getHoldStartTime() != null && booking.getHoldStartTime().plus(Duration.ofMinutes(15)).isAfter(Instant.now())) {
                        throw new ResourceAlreadyExistsException("Slot is temporarily held. Try again later.");
                    }
                }
            }

            // Prevent user from booking same time slot for 2 different mentors
            List<Booking> bookings = bookingRepository.findByMenteeIdAndSessionStartTimeBetweenAndPaymentStatus(bookingDTO.getMenteeId(), utcStart, utcEnd, PaymentStatus.COMPLETED);

            if (!(bookings.isEmpty())) {

                List<Long> bookedSlotIds = bookings.stream().map(Booking::getTimeSlotId).toList();

                // Avoid repetitive DB calls by directly collecting whole Slots
                List<FixedTimeSlotNew> bookedSlots = fixedTimeSlotNewRepository.findAllById(bookedSlotIds);

                for (FixedTimeSlotNew bookedSlot : bookedSlots) {

                    // Converting booked slot in UTC to booked slot in mentee time zone with date
                    LocalTime bookedSlotTime = bookedSlot.getTimeStart()
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(zoneId)
                            .toLocalTime();

                    ZonedDateTime bookedStartZoned = bookingDate.atTime(bookedSlotTime).atZone(zoneId);
                    ZonedDateTime bookedEndZoned = bookedStartZoned.plusHours(1);

                    // Now comparing the booked slot in mentee time zone with current slot in mentee time zone
                    if (currentSlotStartDateTime.isBefore(bookedEndZoned) && bookedStartZoned.isBefore(currentSlotEndDateTime)) {
                        throw new ResourceAlreadyExistsException(OVERLAPS_WITH_EXISTING_BOOKED_SLOT);
                    }


                }
            }

            Booking booking = Booking.builder()
                    .mentorId(bookingDTO.getMentorId())
                    .menteeId(bookingDTO.getMenteeId())
                    .timeSlotId(bookingDTO.getTimeSlotId())
                    .category(bookingDTO.getCategory())
                    .connectMethod(bookingDTO.getConnectMethod())
                    .amount(bookingDTO.getAmount())
                    .currency(bookingDTO.getCurrency())
                    .productName(bookingDTO.getProductName())
                    .quantity(bookingDTO.getQuantity())
                    .paymentStatus(PaymentStatus.HOLD)
                    .holdStartTime(Instant.now())
                    .mentorMeetLink(bookingDTO.getMentorMeetLink())
                    .userMeetLink(bookingDTO.getUserMeetLink())
                    .menteeTimezone(menteeProfile.getTimeZone())
                    .build();


            // To send the booking id in meta data we are saving it only some details first
            // we receive date as string but spring converts it into date automatically if date is in this format yyyy-mm-dd
            Booking savedBooking = bookingRepository.save(booking);

            // 4. Get mentor and user emails from DB
            String menteeEmail = menteeProfile.getEmail();

            Instant sessionEnd = currentSlotEndDateTime.toInstant();

            // 4. Convert to ISO strings
            String sessionStartStr = sessionStart.toString();
            String sessionEndStr = sessionEnd.toString();

            MentorProfile mentorProfile = mentorProfileRepository.findById(bookingDTO.getMentorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID " + bookingDTO.getMentorId()));

            //Stripe session creation
            Session session = null;
            SessionCreateParams.LineItem.PriceData.ProductData productData
                    = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(booking.getProductName()).build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(booking.getCurrency() == null ? "CAD" : booking.getCurrency())
                    .setUnitAmount(Math.round(booking.getAmount() * 100)) // To paise or cents
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(booking.getQuantity())
                    .setPriceData(priceData)
                    .build();

            // If we not give payment method type, by default it takes card,
            // If Wants to support UPI, AfterPay, or others, we need to specify in the method
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "/{CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl + "/{CHECKOUT_SESSION_ID}")
                    .addLineItem(lineItem)
                    .putMetadata("mentorEmail", mentorProfile.getEmail())
                    .putMetadata("menteeEmail", menteeEmail)
                    .putMetadata("mentorName", mentorProfile.getName())
                    .putMetadata("menteeName", menteeProfile.getName())
                    .putMetadata("sessionStart", sessionStartStr)
                    .putMetadata("sessionEnd", sessionEndStr)
                    .putMetadata("mentorTimezone", mentorProfile.getTimezone())
                    .putMetadata("menteeTimezone", menteeProfile.getTimeZone())
                    .putMetadata("bookingId", String.valueOf(savedBooking.getId()));
                    //.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)

            SessionCreateParams params = builder.build();

            session = Session.create(params);

            savedBooking.setStripeSessionId(session.getId());
            savedBooking.setSessionStartTime(sessionStart);
            bookingRepository.save(savedBooking);

            PaymentResponse paymentResponse = PaymentResponse.builder()
                    .status("Success")
                    .message("Payment session created")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

            return CommonResponse.<PaymentResponse>builder()
                    .message(LOADED_SESSION_URL)
                    .status(STATUS_TRUE)
                    .data(paymentResponse)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException | StripeException | InvalidFieldValueException e){
            throw e; // it will catch by global exception handler
        } catch (Exception e){
            throw new UnexpectedServerException("Error while creating payment session:" + e.getMessage());
        }


    }

    // Don't remove this method
    public String refundBooking(String paymentIntentId) throws StripeException, ResourceNotFoundException {


        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                .build();

        Refund refund = Refund.create(params);

        return refund.getId();
    }

}
