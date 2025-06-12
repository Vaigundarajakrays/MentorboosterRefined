//package com.mentorboosters.app.controller;
//
//import com.mentorboosters.app.dto.BookingDTO;
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.Booking;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.service.BookingService;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class BookingController {
//
//    private final BookingService bookingService;
//
//    public BookingController(BookingService bookingService){this.bookingService=bookingService;}
//
////    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_MENTOR')")
////    @PostMapping("/saveBooking")
////    public CommonResponse<Booking> saveBooking(@RequestBody Booking booking) throws UnexpectedServerException, ResourceNotFoundException {
////        return bookingService.saveBooking(booking);
////    }
//
//    @GetMapping("getBookingsByUser/{userId}")
//    public CommonResponse<List<BookingDTO>> getBookingsByUserId(@PathVariable Long userId, @RequestParam LocalDate date) throws UnexpectedServerException, ResourceNotFoundException {
//        return bookingService.getBookingsByUserId(userId, date);
//    }
//
//    @GetMapping("getBookingsByMentor/{mentorId}")
//    public CommonResponse<List<BookingDTO>> getBookingsByMentorId(@PathVariable Long mentorId, @RequestParam LocalDate date) throws UnexpectedServerException, ResourceNotFoundException {
//        return bookingService.getBookingsByMentorId(mentorId, date);
//    }
//
//    @GetMapping("/hasUpcomingBooking")
//    public CommonResponse<Boolean> hasUpcomingBooking(@RequestParam Long userId, @RequestParam Long mentorId) throws UnexpectedServerException {
//        return bookingService.hasUpcomingBooking(userId, mentorId);
//    }
//}
