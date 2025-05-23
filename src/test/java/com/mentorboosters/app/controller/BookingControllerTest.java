//package com.mentorboosters.app.controller;
//
//import com.mentorboosters.app.dto.BookingDTO;
//import com.mentorboosters.app.enumUtil.Role;
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.*;
//import com.mentorboosters.app.repository.BookingRepository;
//import com.mentorboosters.app.repository.FixedTimeSlotRepository;
//import com.mentorboosters.app.repository.MentorRepository;
//import com.mentorboosters.app.repository.UsersRepository;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.service.BookingService;
//import com.mentorboosters.app.zoom.ZoomMeetingService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.sql.Time;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//import static com.mentorboosters.app.util.Constant.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class BookingControllerTest {
//
//    @Mock
//    BookingRepository bookingRepository;
//
//    @Mock
//    FixedTimeSlotRepository fixedTimeSlotRepository;
//
//    @Mock
//    MentorRepository mentorRepository;
//
//    @Mock
//    UsersRepository usersRepository;
//
//    @Mock
//    ZoomMeetingService zoomMeetingService;
//
//    @InjectMocks
//    BookingService bookingService;
//
//    private Booking booking, savedBooking;
//    private FixedTimeSlot currentSlot, slot1;
//    private Mentor mentor1;
//
//    @BeforeEach
//    void setUp(){
//
//        booking = Booking.builder()
//                .userId(1L)
//                .mentorId(2L)
//                .timeSlotId(3L)
//                .bookingDate(LocalDate.now().plusDays(1))
//                .category("Design")
//                .connectMethod("Zoom")
//                .googleMeetLink(null)
//                .build();
//
//        savedBooking = Booking.builder()
//                .id(1L)
//                .userId(1L)
//                .mentorId(2L)
//                .timeSlotId(3L)
//                .bookingDate(LocalDate.now().plusDays(1))
//                .category("Design")
//                .connectMethod("Zoom")
//                .googleMeetLink(null)
//                .build();
//        savedBooking.setCreatedAt(LocalDateTime.now());
//        savedBooking.setUpdatedAt(LocalDateTime.now());
//
//        currentSlot = FixedTimeSlot.builder()
//                .id(3L)
//                .timeStart(LocalTime.of(10, 0))
//                .timeEnd(LocalTime.of(11, 0))
//                .build();
//
//        Certificate cert = Certificate.builder()
//                .id(1L)
//                .name("AWS Certified")
//                .provideBy("Amazon")
//                .createDate("2023-01-01")
//                .imageUrl("https://example.com/certificate.jpg")
//                .build();
//        cert.setCreatedAt(LocalDateTime.now());
//        cert.setUpdatedAt(LocalDateTime.now());
//
//        Experience exp = Experience.builder()
//                .id(1L)
//                .role("Software Engineer")
//                .companyName("Tech Corp")
//                .startDate(LocalDate.of(2020, 1, 1))
//                .endDate(LocalDate.of(2023, 1, 1))
//                .description("Worked on cloud-based solutions.")
//                .build();
//        exp.setCreatedAt(LocalDateTime.now());
//        exp.setUpdatedAt(LocalDateTime.now());
//
//        slot1 = FixedTimeSlot.builder()
//                .id(1L)
//                .timeStart(Time.valueOf("10:00:00").toLocalTime())
//                .timeEnd(Time.valueOf("11:00:00").toLocalTime())
//                .build();
//        slot1.setCreatedAt(LocalDateTime.now());
//        slot1.setUpdatedAt(LocalDateTime.now());
//
//        Category cat1 = Category.builder()
//                .id(1L)
//                .name("Design")
//                .icon("Awesome.code")
//                .build();
//        cat1.setCreatedAt(LocalDateTime.now());
//        cat1.setUpdatedAt(LocalDateTime.now());
//
//        mentor1 = Mentor.builder()
//                .id(1L)
//                .name("Joe1")
//                .email("joe1@gmail.com")
//                .gender("Male")
//                .avatarUrl("https://example.com/avatar.jpg")
//                .bio("Experienced mentor")
//                .role("Software Engineer")
//                .freePrice(50.0)
//                .freeUnit("hour")
//                .verified(true)
//                .rate(4.9)
//                .numberOfMentoree(20)
//                .certificates(List.of(cert))
//                .experiences(List.of(exp))
//                .timeSlots(List.of(slot1))
//                .categories(List.of(cat1))
//                .build();
//        mentor1.setCreatedAt(LocalDateTime.now());
//        mentor1.setUpdatedAt(LocalDateTime.now());
//
//    }
//
//    @Test
//    void saveBooking_shouldReturnSuccess_whenBookingIsSavedSuccessfully() throws Exception {
//        // Arrange
//        Long userId = 1L;
//        Long mentorId = 2L;
//        Long timeSlotId = 3L;
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
//
//        FixedTimeSlot fixedTimeSlot = FixedTimeSlot.builder()
//                .id(timeSlotId)
//                .timeStart(LocalTime.of(10, 0))
//                .timeEnd(LocalTime.of(11, 0))
//                .build();
//
//        // Mocking the necessary repository and service methods
//        when(fixedTimeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(fixedTimeSlot));
//        when(bookingRepository.findByUserIdAndBookingDate(userId, tomorrow)).thenReturn(List.of());  // Use tomorrow's date here
//        when(bookingRepository.save(booking)).thenReturn(savedBooking);
//        when(usersRepository.findById(userId)).thenReturn(Optional.of(Users.builder().emailId("user@example.com").build()));
//        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(Mentor.builder().email("mentor@example.com").build()));
//
//        // Act
//        CommonResponse<Booking> response = bookingService.saveBooking(booking);
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(SUCCESSFULLY_ADDED, response.getMessage());
//
//        Booking returnedBooking = response.getData();
//        assertNotNull(returnedBooking);
//        assertEquals(savedBooking.getId(), returnedBooking.getId());
//        assertEquals(savedBooking.getUserId(), returnedBooking.getUserId());
//        assertEquals(savedBooking.getMentorId(), returnedBooking.getMentorId());
//        assertEquals(savedBooking.getTimeSlotId(), returnedBooking.getTimeSlotId());
//        assertEquals(savedBooking.getBookingDate(), returnedBooking.getBookingDate());
//        assertEquals(savedBooking.getCategory(), returnedBooking.getCategory());
//        assertEquals(savedBooking.getConnectMethod(), returnedBooking.getConnectMethod());
//        assertEquals(savedBooking.getGoogleMeetLink(), returnedBooking.getGoogleMeetLink());
//        assertNotNull(response.getData().getCreatedAt());
//        assertNotNull(response.getData().getUpdatedAt());
//
//        verify(fixedTimeSlotRepository).findById(timeSlotId);
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, tomorrow);
//        verify(bookingRepository).save(booking);
//        verify(usersRepository).findById(userId);
//        verify(mentorRepository).findById(mentorId);
//        verify(zoomMeetingService).createZoomMeetingAndNotify(anyString(), anyString(), any(), any());
//    }
//
//    @Test
//    void saveBooking_shouldThrowResourceNotFoundException_whenTimeSlotIsNotFound() {
//        Long userId = 1L;
//        Long mentorId = 2L;
//        Long timeSlotId = 3L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        when(fixedTimeSlotRepository.findById(timeSlotId)).thenReturn(Optional.empty());
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            bookingService.saveBooking(booking);
//        });
//
//        assertNotNull(exception);
//        assertTrue(exception.getMessage().contains(TIMESLOT_NOT_FOUND_WITH_ID));
//
//        verify(fixedTimeSlotRepository).findById(timeSlotId);
//    }
//
//    @Test
//    void saveBooking_shouldReturnConflict_whenTimeSlotOverlapsWithExistingBooking() throws ResourceNotFoundException, UnexpectedServerException {
//        // Arrange
//        Long userId = 1L;
//        Long mentorId = 2L;
//        Long timeSlotId = 3L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        List<Booking> existingBookings = List.of(
//                Booking.builder()
//                        .userId(userId)
//                        .mentorId(mentorId)
//                        .timeSlotId(timeSlotId)
//                        .bookingDate(bookingDate)
//                        .category("Design")
//                        .connectMethod("Zoom")
//                        .googleMeetLink(null)
//                        .build()
//        );
//
//        List<Long> bookedSlotIds = existingBookings.stream().map(Booking::getTimeSlotId).toList();
//        List<FixedTimeSlot> bookedSlots = List.of(currentSlot);
//
//        when(fixedTimeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(currentSlot));
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenReturn(existingBookings);
//        when(fixedTimeSlotRepository.findAllById(bookedSlotIds)).thenReturn(bookedSlots);
//
//        // Act
//        CommonResponse<Booking> response = bookingService.saveBooking(booking);
//
//        // Assert
//        assertNotNull(response);
//        assertFalse(response.getStatus());
//        assertEquals(CONFLICT_CODE, response.getStatusCode());
//        assertEquals(OVERLAPS_WITH_EXISTING_BOOKED_SLOT, response.getMessage());
//
//        verify(fixedTimeSlotRepository).findById(timeSlotId);
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(fixedTimeSlotRepository).findAllById(bookedSlotIds);
//    }
//
//    @Test
//    void saveBooking_shouldThrowResourceNotFoundException_whenUserNotFound() throws Exception {
//        // Arrange
//        Long userId = 1L;
//        Long timeSlotId = 3L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        when(fixedTimeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(currentSlot));
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenReturn(List.of());
//        when(bookingRepository.save(booking)).thenReturn(booking);
//
//        when(usersRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // Act + Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            bookingService.saveBooking(booking);
//        });
//
//        assertTrue(exception.getMessage().contains("User not found with ID " + userId));
//
//        verify(fixedTimeSlotRepository).findById(timeSlotId);
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(bookingRepository).save(booking);
//        verify(usersRepository).findById(userId);
//    }
//
//    @Test
//    void saveBooking_shouldThrowResourceNotFoundException_whenMentorNotFound() throws UnexpectedServerException, ResourceNotFoundException {
//        // Arrange
//        Long userId = 1L;
//        Long mentorId = 2L;
//        Long timeSlotId = 3L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        when(fixedTimeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(currentSlot));
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenReturn(List.of());
//        when(bookingRepository.save(booking)).thenReturn(booking);
//
//        when(usersRepository.findById(userId)).thenReturn(Optional.of(
//                Users.builder().id(userId).emailId("user@example.com").build()
//        ));
//        when(mentorRepository.findById(mentorId)).thenReturn(Optional.empty());
//
//        // Act + Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            bookingService.saveBooking(booking);
//        });
//
//        assertTrue(exception.getMessage().contains("Mentor not found with ID " + mentorId));
//
//        // Verify
//        verify(fixedTimeSlotRepository).findById(timeSlotId);
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(bookingRepository).save(booking);
//        verify(usersRepository).findById(userId);
//        verify(mentorRepository).findById(mentorId);
//    }
//
//    @Test
//    void saveBooking_shouldReturnUnexpectedServerException_whenAnUnexpectedErrorOccurs() throws ResourceNotFoundException, UnexpectedServerException {
//        // Arrange
//        Long userId = 1L;
//        Long timeSlotId = 3L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        when(fixedTimeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(currentSlot));
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenReturn(List.of());
//
//        when(bookingRepository.save(booking)).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            bookingService.saveBooking(booking);
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_BOOKING_TIME_SLOT));
//
//        verify(fixedTimeSlotRepository).findById(timeSlotId);
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(bookingRepository).save(booking);
//    }
//
//    @Test
//    void getBookingsByUserId_shouldReturnBookings_whenUserHasBookings() throws UnexpectedServerException, ResourceNotFoundException {
//        // Arrange
//        Long userId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .userId(userId)
//                .mentorId(mentor1.getId())
//                .timeSlotId(slot1.getId())
//                .bookingDate(bookingDate)
//                .category("Design")
//                .connectMethod("Zoom")
//                .googleMeetLink("https://meet.google.com/xyz")
//                .build();
//
//        FixedTimeSlot fixedTimeSlot = FixedTimeSlot.builder()
//                .id(slot1.getId())
//                .timeStart(slot1.getTimeStart())
//                .timeEnd(slot1.getTimeEnd())
//                .build();
//
//        Mentor mentor = mentor1;
//
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenReturn(List.of(booking));
//        when(fixedTimeSlotRepository.findById(booking.getTimeSlotId())).thenReturn(Optional.of(fixedTimeSlot));
//        when(mentorRepository.findById(booking.getMentorId())).thenReturn(Optional.of(mentor));
//
//        // Act
//        CommonResponse<List<BookingDTO>> response = bookingService.getBookingsByUserId(userId, bookingDate);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(STATUS_TRUE, response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(1, response.getData().size());
//        assertEquals("Design", response.getData().get(0).getCategory());
//        assertEquals(mentor.getName(), response.getData().get(0).getMentorName());
//        assertEquals(booking.getGoogleMeetLink(), response.getData().get(0).getGMeetLink());
//        assertEquals(slot1.getTimeStart(), response.getData().get(0).getTimeSlotStart());
//        assertEquals(slot1.getTimeEnd(), response.getData().get(0).getTimeSlotEnd());
//
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(fixedTimeSlotRepository).findById(booking.getTimeSlotId());
//        verify(mentorRepository).findById(booking.getMentorId());
//    }
//
//    @Test
//    void getBookingsByUserId_shouldReturnNoBookings_whenNoBookingsExist() throws UnexpectedServerException, ResourceNotFoundException {
//        // Arrange
//        Long userId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenReturn(List.of());
//
//        // Act
//        CommonResponse<List<BookingDTO>> response = bookingService.getBookingsByUserId(userId, bookingDate);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(STATUS_FALSE, response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertTrue(response.getData().isEmpty());
//        assertEquals(NO_BOOKED_SLOTS_AVAILABLE, response.getMessage());
//
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(fixedTimeSlotRepository, never()).findById(anyLong());
//        verify(mentorRepository, never()).findById(anyLong());
//    }
//
//    @Test
//    void getBookingsByUserId_shouldReturnUnexpectedServerException_whenExceptionOccurs() {
//        // Arrange
//        Long userId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            bookingService.getBookingsByUserId(userId, bookingDate);
//        });
//        assertTrue(exception.getMessage().contains(ERROR_FETCHING_BOOKED_SLOTS));
//
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(fixedTimeSlotRepository, never()).findById(anyLong());
//        verify(mentorRepository, never()).findById(anyLong());
//    }
//
//    @Test
//    void getBookingsByUserId_shouldReturnResourceNotFoundException_whenFixedTimeSlotNotFound() throws UnexpectedServerException {
//        // Arrange
//        Long userId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .userId(userId)
//                .mentorId(mentor1.getId())
//                .timeSlotId(99L)
//                .bookingDate(bookingDate)
//                .category("Design")
//                .connectMethod("Zoom")
//                .googleMeetLink("https://meet.google.com/xyz")
//                .build();
//
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenReturn(List.of(booking));
//        when(fixedTimeSlotRepository.findById(booking.getTimeSlotId())).thenReturn(Optional.empty());
//
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            bookingService.getBookingsByUserId(userId, bookingDate);
//        });
//
//        assertTrue(exception.getMessage().contains(TIMESLOT_NOT_FOUND_WITH_ID + booking.getTimeSlotId()));
//
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(fixedTimeSlotRepository).findById(booking.getTimeSlotId());
//
//    }
//
//    @Test
//    void getBookingsByUserId_shouldReturnResourceNotFoundException_whenMentorNotFound() throws UnexpectedServerException {
//        Long userId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .userId(userId)
//                .mentorId(99L)
//                .timeSlotId(3L)
//                .bookingDate(bookingDate)
//                .category("Design")
//                .connectMethod("Zoom")
//                .googleMeetLink("https://meet.google.com/xyz")
//                .build();
//
//        when(bookingRepository.findByUserIdAndBookingDate(userId, bookingDate)).thenReturn(List.of(booking));
//        when(fixedTimeSlotRepository.findById(booking.getTimeSlotId())).thenReturn(Optional.of(slot1));
//        when(mentorRepository.findById(booking.getMentorId())).thenReturn(Optional.empty());
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            bookingService.getBookingsByUserId(userId, bookingDate);
//        });
//
//        //assert
//        assertTrue(exception.getMessage().contains(MENTOR_NOT_FOUND_WITH_ID + booking.getMentorId()));
//
//        verify(bookingRepository).findByUserIdAndBookingDate(userId, bookingDate);
//        verify(fixedTimeSlotRepository).findById(booking.getTimeSlotId());
//        verify(mentorRepository).findById(booking.getMentorId());
//    }
//
//    @Test
//    void getBookingsByMentorId_shouldReturnBookingsSuccessfully() throws UnexpectedServerException, ResourceNotFoundException {
//        Long mentorId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .mentorId(mentorId)
//                .userId(2L)
//                .timeSlotId(3L)
//                .bookingDate(bookingDate)
//                .category("Design")
//                .connectMethod("Zoom")
//                .googleMeetLink("https://meet.google.com/abc")
//                .build();
//
//        FixedTimeSlot slot = FixedTimeSlot.builder()
//                .id(3L)
//                .timeStart(LocalTime.of(10, 0))
//                .timeEnd(LocalTime.of(11, 0))
//                .build();
//
//        Users user = Users.builder()
//                .id(2L)
//                .userName("user123")
//                .name("John Doe")
//                .emailId("john@example.com")
//                .password("password")
//                .age(25)
//                .gender("Male")
//                .role(Role.USER)
//                .build();
//
//        when(bookingRepository.findByMentorIdAndBookingDate(mentorId, bookingDate)).thenReturn(List.of(booking));
//        when(fixedTimeSlotRepository.findById(booking.getTimeSlotId())).thenReturn(Optional.of(slot));
//        when(usersRepository.findById(booking.getUserId())).thenReturn(Optional.of(user));
//
//        CommonResponse<List<BookingDTO>> response = bookingService.getBookingsByMentorId(mentorId, bookingDate);
//
//        assertEquals(STATUS_TRUE, response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertEquals(1, response.getData().size());
//        BookingDTO dto = response.getData().get(0);
//        assertEquals(booking.getId(), dto.getId());
//        assertEquals(booking.getBookingDate(), dto.getBookingDate());
//        assertEquals(slot.getTimeStart(), dto.getTimeSlotStart());
//        assertEquals(slot.getTimeEnd(), dto.getTimeSlotEnd());
//        assertEquals(user.getName(), dto.getUserName());
//
//        verify(bookingRepository).findByMentorIdAndBookingDate(mentorId, bookingDate);
//        verify(fixedTimeSlotRepository).findById(booking.getTimeSlotId());
//        verify(usersRepository).findById(booking.getUserId());
//    }
//
//    @Test
//    void getBookingsByMentorId_shouldReturnEmptyList_whenNoBookingsFound() throws UnexpectedServerException, ResourceNotFoundException {
//        Long mentorId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        when(bookingRepository.findByMentorIdAndBookingDate(mentorId, bookingDate)).thenReturn(List.of());
//
//        CommonResponse<List<BookingDTO>> response = bookingService.getBookingsByMentorId(mentorId, bookingDate);
//
//        assertEquals(STATUS_FALSE, response.getStatus());
//        assertEquals(SUCCESS_CODE, response.getStatusCode());
//        assertTrue(response.getData().isEmpty());
//
//        verify(bookingRepository).findByMentorIdAndBookingDate(mentorId, bookingDate);
//    }
//
//    @Test
//    void getBookingsByMentorId_shouldThrowResourceNotFoundException_whenFixedTimeSlotNotFound() {
//        Long mentorId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .mentorId(mentorId)
//                .userId(2L)
//                .timeSlotId(99L)
//                .bookingDate(bookingDate)
//                .build();
//
//        when(bookingRepository.findByMentorIdAndBookingDate(mentorId, bookingDate)).thenReturn(List.of(booking));
//        when(fixedTimeSlotRepository.findById(booking.getTimeSlotId())).thenReturn(Optional.empty());
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            bookingService.getBookingsByMentorId(mentorId, bookingDate);
//        });
//
//        assertTrue(exception.getMessage().contains(TIMESLOT_NOT_FOUND_WITH_ID + booking.getTimeSlotId()));
//
//        verify(bookingRepository).findByMentorIdAndBookingDate(mentorId, bookingDate);
//        verify(fixedTimeSlotRepository).findById(booking.getTimeSlotId());
//    }
//
//    @Test
//    void getBookingsByMentorId_shouldThrowResourceNotFoundException_whenUserNotFound() {
//        Long mentorId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .mentorId(mentorId)
//                .userId(2L)
//                .timeSlotId(3L)
//                .bookingDate(bookingDate)
//                .build();
//
//        FixedTimeSlot slot = FixedTimeSlot.builder()
//                .id(3L)
//                .timeStart(LocalTime.of(10, 0))
//                .timeEnd(LocalTime.of(11, 0))
//                .build();
//
//        when(bookingRepository.findByMentorIdAndBookingDate(mentorId, bookingDate)).thenReturn(List.of(booking));
//        when(fixedTimeSlotRepository.findById(booking.getTimeSlotId())).thenReturn(Optional.of(slot));
//        when(usersRepository.findById(booking.getUserId())).thenReturn(Optional.empty());
//
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            bookingService.getBookingsByMentorId(mentorId, bookingDate);
//        });
//
//        assertTrue(exception.getMessage().contains(USER_NOT_FOUND_WITH_ID + booking.getUserId()));
//
//        verify(bookingRepository).findByMentorIdAndBookingDate(mentorId, bookingDate);
//        verify(fixedTimeSlotRepository).findById(booking.getTimeSlotId());
//        verify(usersRepository).findById(booking.getUserId());
//    }
//
//    @Test
//    void getBookingsByMentorId_shouldThrowUnexpectedServerException_whenUnexpectedErrorOccurs() {
//        Long mentorId = 1L;
//        LocalDate bookingDate = LocalDate.now().plusDays(1);
//
//        when(bookingRepository.findByMentorIdAndBookingDate(mentorId, bookingDate)).thenThrow(new RuntimeException("Database error"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            bookingService.getBookingsByMentorId(mentorId, bookingDate);
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_FETCHING_BOOKED_SLOTS));
//
//        verify(bookingRepository).findByMentorIdAndBookingDate(mentorId, bookingDate);
//    }
//
//    @Test //Used assertAll because even if one assert fails execution wont stop, So even if one assertion fails, JUnit will still check all other assertions - Faster debugging
//    void hasUpcomingBooking_shouldReturnTrue_whenBookingIsTomorrow() throws UnexpectedServerException {
//        Long userId = 1L;
//        Long mentorId = 2L;
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .userId(userId)
//                .mentorId(mentorId)
//                .timeSlotId(1L)
//                .bookingDate(tomorrow)
//                .category("Technology")
//                .connectMethod("Zoom")
//                .googleMeetLink("https://meet.google.com/abc")
//                .build();
//
//        when(bookingRepository.findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, LocalDate.now()))
//                .thenReturn(List.of(booking));
//
//        CommonResponse<Boolean> response = bookingService.hasUpcomingBooking(userId, mentorId);
//
//        assertAll(
//                () -> assertNotNull(response),
//                () -> assertTrue(response.getStatus()),
//                () -> assertEquals(SUCCESS_CODE, response.getStatusCode()),
//                () -> assertEquals(BOOKING_CHECK_COMPLETED, response.getMessage()),
//                () -> assertTrue(response.getData())
//        );
//
//        verify(bookingRepository).findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, LocalDate.now());
//    }
//
//    @Test
//    void hasUpcomingBooking_shouldReturnFalse_whenBookingIsTodayButTimeIsOver() throws UnexpectedServerException {
//        Long userId = 1L;
//        Long mentorId = 2L;
//        LocalDate today = LocalDate.now();
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .userId(userId)
//                .mentorId(mentorId)
//                .bookingDate(today)
//                .timeSlotId(1L)
//                .build();
//
//        FixedTimeSlot fixedTimeSlot = FixedTimeSlot.builder()
//                .id(1L)
//                .timeStart(LocalTime.now().minusHours(2))
//                .timeEnd(LocalTime.now().minusHours(1))
//                .build();
//
//        when(bookingRepository.findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, today))
//                .thenReturn(List.of(booking));
//        when(fixedTimeSlotRepository.findById(1L)).thenReturn(Optional.of(fixedTimeSlot));
//
//        CommonResponse<Boolean> response = bookingService.hasUpcomingBooking(userId, mentorId);
//
//        assertAll(
//                () -> assertFalse(response.getData()),
//                () -> assertEquals(STATUS_TRUE, response.getStatus()),
//                () -> assertEquals(SUCCESS_CODE, response.getStatusCode()),
//                () -> assertEquals(BOOKING_CHECK_COMPLETED, response.getMessage())
//        );
//
//        verify(bookingRepository).findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, today);
//        verify(fixedTimeSlotRepository).findById(1L);
//    }
//
//    @Test
//    void hasUpcomingBooking_shouldReturnTrue_whenBookingIsTodayAndOngoing() throws UnexpectedServerException {
//        Long userId = 1L;
//        Long mentorId = 2L;
//        LocalDate today = LocalDate.now();
//
//        Booking booking = Booking.builder()
//                .id(1L)
//                .userId(userId)
//                .mentorId(mentorId)
//                .bookingDate(today)
//                .timeSlotId(1L)
//                .build();
//
//        FixedTimeSlot fixedTimeSlot = FixedTimeSlot.builder()
//                .id(1L)
//                .timeStart(LocalTime.now().minusMinutes(30))
//                .timeEnd(LocalTime.now().plusMinutes(30))
//                .build();
//
//        when(bookingRepository.findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, today))
//                .thenReturn(List.of(booking));
//        when(fixedTimeSlotRepository.findById(1L)).thenReturn(Optional.of(fixedTimeSlot));
//
//        CommonResponse<Boolean> response = bookingService.hasUpcomingBooking(userId, mentorId);
//
//        assertAll(
//                () -> assertTrue(response.getData()),
//                () -> assertEquals(STATUS_TRUE, response.getStatus()),
//                () -> assertEquals(SUCCESS_CODE, response.getStatusCode()),
//                () -> assertEquals(BOOKING_CHECK_COMPLETED, response.getMessage())
//        );
//
//        verify(bookingRepository).findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, today);
//        verify(fixedTimeSlotRepository).findById(1L);
//    }
//
//    @Test
//    void hasUpcomingBooking_shouldThrowUnexpectedServerException_whenExceptionOccurs() {
//        Long userId = 1L;
//        Long mentorId = 2L;
//
//        when(bookingRepository.findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, LocalDate.now()))
//                .thenThrow(new RuntimeException("Database down"));
//
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class,
//                () -> bookingService.hasUpcomingBooking(userId, mentorId)
//        );
//
//        assertTrue(exception.getMessage().contains(ERROR_CHECKING_TIME_SLOT));
//
//        verify(bookingRepository).findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, LocalDate.now());
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//}
