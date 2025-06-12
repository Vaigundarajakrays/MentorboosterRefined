//package com.mentorboosters.app.service;
//
//import com.mentorboosters.app.dto.BookingDTO;
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.Booking;
//import com.mentorboosters.app.model.FixedTimeSlot;
//import com.mentorboosters.app.model.Mentor;
//import com.mentorboosters.app.model.Users;
//import com.mentorboosters.app.repository.BookingRepository;
//import com.mentorboosters.app.repository.FixedTimeSlotRepository;
//import com.mentorboosters.app.repository.MentorRepository;
//import com.mentorboosters.app.repository.UsersRepository;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.zoom.ZoomMeetingService;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static com.mentorboosters.app.util.Constant.*;
//
//@Service
//public class BookingService {
//
//    private final BookingRepository bookingRepository;
//    private final FixedTimeSlotRepository fixedTimeSlotRepository;
//    private final MentorRepository mentorRepository;
//    private final UsersRepository usersRepository;
//    private final ZoomMeetingService zoomMeetingService;
//
//    public BookingService(BookingRepository bookingRepository, FixedTimeSlotRepository fixedTimeSlotRepository, MentorRepository mentorRepository, UsersRepository usersRepository, ZoomMeetingService zoomMeetingService){
//        this.bookingRepository=bookingRepository;
//        this.fixedTimeSlotRepository=fixedTimeSlotRepository;
//        this.mentorRepository=mentorRepository;
//        this.usersRepository=usersRepository;
//        this.zoomMeetingService=zoomMeetingService;
//    }
//
////    public void saveBooking(Booking booking) throws UnexpectedServerException, ResourceNotFoundException {
////
////        FixedTimeSlot currentSlot = fixedTimeSlotRepository.findById(booking.getTimeSlotId()).orElseThrow(() -> new ResourceNotFoundException(TIMESLOT_NOT_FOUND_WITH_ID + booking.getTimeSlotId()));
////
////        try {
////            // Prevent user from booking same time slot for 2 different mentors
////            List<Booking> bookings = bookingRepository.findByUserIdAndBookingDateAndPaymentStatus(booking.getUserId(), booking.getBookingDate(), "complete");
////
////            if(!(bookings.isEmpty())) {
////
////                List<Long> bookedSlotIds = bookings.stream().map(Booking::getTimeSlotId).toList();
////
////                // Avoid repetitive DB calls by directly collecting whole Slots
////                List<FixedTimeSlot> bookedSlots = fixedTimeSlotRepository.findAllById(bookedSlotIds);
////
////                for (FixedTimeSlot bookedSlot : bookedSlots) {
////
////                    // Checking current slot and booked slots are overlapping, formula is start1 < end2 && start2 < end1
////                    if (currentSlot.getTimeStart().isBefore(bookedSlot.getTimeEnd()) && bookedSlot.getTimeStart().isBefore(currentSlot.getTimeEnd())) {
////
////                        CommonResponse.<Booking>builder()
////                                .message(OVERLAPS_WITH_EXISTING_BOOKED_SLOT)
////                                .status(STATUS_FALSE)
////                                .statusCode(CONFLICT_CODE)
////                                .build();
////                        return;
////                    }
////                }
////            }
////
////            booking.setPaymentStatus("pending");
////
////            // we receive date as string but spring converts it into date automatically if date is in this format yyyy-mm-dd
////            Booking savedBooking = bookingRepository.save(booking);
////
////            Date startTime = Date.from(currentSlot.getTimeStart().atDate(booking.getBookingDate())
////                    .atZone(ZoneId.systemDefault()).toInstant());
////            Date endTime = Date.from(currentSlot.getTimeEnd().atDate(booking.getBookingDate())
////                    .atZone(ZoneId.systemDefault()).toInstant());
////
////            // 4. Get mentor and user emails from DB
////            String userEmail = usersRepository.findById(booking.getUserId())
////                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + booking.getUserId()))
////                    .getEmailId();
////
////            String mentorEmail = mentorRepository.findById(booking.getMentorId())
////                    .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with ID " + booking.getMentorId()))
////                    .getEmail();
////
////            // 5. Create Zoom meeting and notify via email
////            zoomMeetingService.createZoomMeetingAndNotify(mentorEmail, userEmail, startTime, endTime);
////
////            CommonResponse.<Booking>builder()
////                    .message(SUCCESSFULLY_ADDED)
////                    .status(STATUS_TRUE)
////                    .data(savedBooking)
////                    .statusCode(SUCCESS_CODE)
////                    .build();
////
////        }
////
////        catch (ResourceNotFoundException e){
////            throw e;
////        }
////        catch (Exception e){
////            throw new UnexpectedServerException(ERROR_BOOKING_TIME_SLOT + e.getMessage());
////        }
////    }
//
//    public CommonResponse<List<BookingDTO>> getBookingsByUserId(Long userId, LocalDate date) throws UnexpectedServerException, ResourceNotFoundException {
//
//        try {
//
//            List<Booking> bookings = bookingRepository.findByUserIdAndBookingDate(userId, date);
//
//            if (bookings.isEmpty()) {
//                return CommonResponse.<List<BookingDTO>>builder()
//                        .message(NO_BOOKED_SLOTS_AVAILABLE)
//                        .status(STATUS_FALSE)
//                        .data(List.of())
//                        .statusCode(SUCCESS_CODE)
//                        .build();
//            }
//
//            List<BookingDTO> bookingDTOS = new ArrayList<>();
//            for (Booking booking : bookings) {
//                FixedTimeSlot slot = fixedTimeSlotRepository.findById(booking.getTimeSlotId()).orElseThrow(() -> new ResourceNotFoundException(TIMESLOT_NOT_FOUND_WITH_ID + booking.getTimeSlotId()));
//                Mentor mentor = mentorRepository.findById(booking.getMentorId()).orElseThrow(() -> new ResourceNotFoundException(MENTOR_NOT_FOUND_WITH_ID + booking.getMentorId()));
//
//                BookingDTO dto = new BookingDTO();
//                dto.setBookingDate(booking.getBookingDate());
//                dto.setId(booking.getId());
//                dto.setCategory(booking.getCategory());
//                dto.setConnectMethod(booking.getConnectMethod());
//                dto.setTimeSlotStart(slot.getTimeStart());
//                dto.setTimeSlotEnd(slot.getTimeEnd());
//                dto.setMentorName(mentor.getName());
//                dto.setZoomMeetLink(booking.getUserMeetLink());
//                dto.setMentorId(mentor.getId());
//                dto.setUserId(userId);
//
//                bookingDTOS.add(dto);
//            }
//            return CommonResponse.<List<BookingDTO>>builder()
//                    .message(LOADED_ALL_BOOKED_SLOTS_FOR_USER)
//                    .status(STATUS_TRUE)
//                    .data(bookingDTOS)
//                    .statusCode(SUCCESS_CODE)
//                    .build();
//
//        }
//
//        catch (ResourceNotFoundException e){
//            throw e;
//        }
//        catch (Exception e){
//            throw new UnexpectedServerException(ERROR_FETCHING_BOOKED_SLOTS + e.getMessage());
//        }
//    }
//
//    public CommonResponse<List<BookingDTO>> getBookingsByMentorId(Long mentorId, LocalDate date) throws UnexpectedServerException, ResourceNotFoundException {
//
//        try {
//
//            List<Booking> bookings = bookingRepository.findByMentorIdAndBookingDate(mentorId, date);
//
//            if (bookings.isEmpty()) {
//                return CommonResponse.<List<BookingDTO>>builder()
//                        .message(NO_BOOKED_SLOTS_AVAILABLE)
//                        .status(STATUS_FALSE)
//                        .data(List.of())
//                        .statusCode(SUCCESS_CODE)
//                        .build();
//            }
//
//            List<BookingDTO> bookingDTOS = new ArrayList<>();
//            for (Booking booking : bookings) {
//                FixedTimeSlot slot = fixedTimeSlotRepository.findById(booking.getTimeSlotId()).orElseThrow(() -> new ResourceNotFoundException(TIMESLOT_NOT_FOUND_WITH_ID + booking.getTimeSlotId()));
//                Users user = usersRepository.findById(booking.getUserId()).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + booking.getUserId()));
//
//                BookingDTO dto = new BookingDTO();
//                dto.setBookingDate(booking.getBookingDate());
//                dto.setId(booking.getId());
//                dto.setCategory(booking.getCategory());
//                dto.setConnectMethod(booking.getConnectMethod());
//                dto.setTimeSlotStart(slot.getTimeStart());
//                dto.setTimeSlotEnd(slot.getTimeEnd());
//                dto.setUserName("user");
//                dto.setZoomMeetLink(booking.getMentorMeetLink());
//                dto.setMentorId(mentorId);
//                dto.setUserId(user.getId());
//
//                bookingDTOS.add(dto);
//            }
//            return CommonResponse.<List<BookingDTO>>builder()
//                    .message(LOADED_ALL_BOOKED_SLOTS_FOR_MENTOR)
//                    .status(STATUS_TRUE)
//                    .data(bookingDTOS)
//                    .statusCode(SUCCESS_CODE)
//                    .build();
//
//        }
//
//        catch (ResourceNotFoundException e){
//            throw e;
//        }
//        catch (Exception e){
//            throw new UnexpectedServerException(ERROR_FETCHING_BOOKED_SLOTS + e.getMessage());
//        }
//
//    }
//
//    public CommonResponse<Boolean> hasUpcomingBooking(Long userId, Long mentorId) throws UnexpectedServerException {
//
//        try {
//
//            LocalDate today = LocalDate.now();
//            LocalTime now = LocalTime.now();
//
//            List<Booking> bookings = bookingRepository.findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, today);
//
//            boolean hasUpcoming = false;
//            for (Booking booking : bookings) {
//
//                LocalDate bookingDate = booking.getBookingDate();
//
//                // Checking whether booked date is in future
//                if (bookingDate.isAfter(today)) {
//                    hasUpcoming = true;
//                    break;
//                }
//
//                //Checking booked date is today
//                if (bookingDate.isEqual(today)) {
//
//                    FixedTimeSlot fixedTimeSlot = fixedTimeSlotRepository.findById(booking.getTimeSlotId()).orElse(null);
//
//                    if (fixedTimeSlot != null && fixedTimeSlot.getTimeEnd().isAfter(now)) {
//
//                        hasUpcoming = true;
//                        break;
//                    }
//                }
//            }
//
//            return CommonResponse.<Boolean>builder()
//                    .message(BOOKING_CHECK_COMPLETED)
//                    .status(STATUS_TRUE)
//                    .data(hasUpcoming)
//                    .statusCode(SUCCESS_CODE)
//                    .build();
//
//        } catch (Exception e) {
//            throw new UnexpectedServerException(ERROR_CHECKING_TIME_SLOT + e.getMessage());
//        }
//    }
//}
