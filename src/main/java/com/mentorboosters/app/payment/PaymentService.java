package com.mentorboosters.app.payment;

import com.mentorboosters.app.exceptionHandling.BookingOverlapException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.model.FixedTimeSlot;
import com.mentorboosters.app.repository.BookingRepository;
import com.mentorboosters.app.repository.FixedTimeSlotRepository;
import com.mentorboosters.app.repository.MentorRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.BookingService;
import com.mentorboosters.app.util.Constant;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class PaymentService {

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    private final BookingRepository bookingRepository;
    private final FixedTimeSlotRepository fixedTimeSlotRepository;
    private final MentorRepository mentorRepository;
    private final UsersRepository usersRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentService(BookingRepository bookingRepository, FixedTimeSlotRepository fixedTimeSlotRepository,MentorRepository mentorRepository, UsersRepository usersRepository){
        this.bookingRepository=bookingRepository;
        this.fixedTimeSlotRepository=fixedTimeSlotRepository;
        this.mentorRepository=mentorRepository;
        this.usersRepository=usersRepository;
    }

    public CommonResponse<PaymentResponse> checkoutProducts(Booking booking) throws StripeException, UnexpectedServerException, ResourceNotFoundException {

        try {

            FixedTimeSlot currentSlot = fixedTimeSlotRepository.findById(booking.getTimeSlotId()).orElseThrow(() -> new ResourceNotFoundException(TIMESLOT_NOT_FOUND_WITH_ID + booking.getTimeSlotId()));

            // Prevent user from booking same time slot for 2 different mentors
            List<Booking> bookings = bookingRepository.findByUserIdAndBookingDateAndPaymentStatus(booking.getUserId(), booking.getBookingDate(), "completed");

            if (!(bookings.isEmpty())) {

                List<Long> bookedSlotIds = bookings.stream().map(Booking::getTimeSlotId).toList();

                // Avoid repetitive DB calls by directly collecting whole Slots
                List<FixedTimeSlot> bookedSlots = fixedTimeSlotRepository.findAllById(bookedSlotIds);

                for (FixedTimeSlot bookedSlot : bookedSlots) {

                    // Checking current slot and booked slots are overlapping, formula is start1 < end2 && start2 < end1
                    if (currentSlot.getTimeStart().isBefore(bookedSlot.getTimeEnd()) && bookedSlot.getTimeStart().isBefore(currentSlot.getTimeEnd())) {

                        throw new BookingOverlapException(OVERLAPS_WITH_EXISTING_BOOKED_SLOT);

                    }
                }
            }

            booking.setPaymentStatus("pending");

            // we receive date as string but spring converts it into date automatically if date is in this format yyyy-mm-dd
            Booking savedBooking = bookingRepository.save(booking);

            Date startTime = Date.from(currentSlot.getTimeStart().atDate(booking.getBookingDate())
                    .atZone(ZoneId.systemDefault()).toInstant());
            Date endTime = Date.from(currentSlot.getTimeEnd().atDate(booking.getBookingDate())
                    .atZone(ZoneId.systemDefault()).toInstant());

            // 4. Get mentor and user emails from DB
            String userEmail = usersRepository.findById(booking.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + booking.getUserId()))
                    .getEmailId();

            String mentorEmail = mentorRepository.findById(booking.getMentorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID " + booking.getMentorId()))
                    .getEmail();

            //Stripe session creation
            Session session = null;
            SessionCreateParams.LineItem.PriceData.ProductData productData
                    = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(booking.getProductName()).build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(booking.getCurrency() == null ? "CAD" : booking.getCurrency())
                    .setUnitAmount(booking.getAmount() * 100) // To paise or cents
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(booking.getQuantity())
                    .setPriceData(priceData)
                    .build();

            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "/{CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl + "/{CHECKOUT_SESSION_ID}")
                    .addLineItem(lineItem)
                    .putMetadata("mentorEmail", mentorEmail)
                    .putMetadata("userEmail", userEmail)
                    .putMetadata("startTime", startTime.toString())
                    .putMetadata("endTime", endTime.toString())
                    .putMetadata("bookingId", String.valueOf(savedBooking.getId()));

            SessionCreateParams params = builder.build();

            try {
                session = Session.create(params);
            } catch (StripeException e) {
                throw new RuntimeException(e.getMessage());
            }

            savedBooking.setStripeSessionId(session.getId());
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

        } catch (BookingOverlapException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException("Error while creating payment session:" + e.getMessage());
        }


    }
}
