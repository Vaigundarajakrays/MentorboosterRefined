package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.TimeSlotDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.model.FixedTimeSlot;
import com.mentorboosters.app.repository.BookingRepository;
import com.mentorboosters.app.repository.FixedTimeSlotRepository;
import com.mentorboosters.app.repository.MentorRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.FixedTimeSlotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.mentorboosters.app.util.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FixedTimeSlotControllerTest {

    @Mock
    FixedTimeSlotRepository fixedTimeSlotRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    MentorRepository mentorRepository;

    @InjectMocks
    FixedTimeSlotService fixedTimeSlotService;

    @Test
    void getAllTimeSlotsOfMentor_shouldReturnTimeSlots_successfully() throws ResourceNotFoundException, UnexpectedServerException, UnexpectedServerException {
        // Arrange
        Long mentorId = 1L;
        LocalDate date = LocalDate.now();

        FixedTimeSlot slot1 = FixedTimeSlot.builder()
                .id(1L)
                .timeStart(LocalTime.of(9, 0))
                .timeEnd(LocalTime.of(10, 0))
                .build();

        FixedTimeSlot slot2 = FixedTimeSlot.builder()
                .id(2L)
                .timeStart(LocalTime.of(10, 0))
                .timeEnd(LocalTime.of(11, 0))
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .mentorId(mentorId)
                .userId(2L)
                .timeSlotId(1L)
                .bookingDate(date)
                .category("General")
                .connectMethod("Zoom")
                .googleMeetLink("meet.google.com/abc-xyz")
                .build();

        List<FixedTimeSlot> slots = List.of(slot1, slot2);
        List<Booking> bookings = List.of(booking);

        when(mentorRepository.existsById(mentorId)).thenReturn(true);
        when(fixedTimeSlotRepository.findByMentorId(mentorId)).thenReturn(slots);
        when(bookingRepository.findByMentorIdAndBookingDate(mentorId, date)).thenReturn(bookings);

        // Act
        CommonResponse<List<TimeSlotDTO>> response = fixedTimeSlotService.getAllTimeSlotsOfMentor(mentorId, date);

        // Assert
        assertNotNull(response);
        assertTrue(response.getStatus());
        assertEquals(200, response.getStatusCode());
        assertEquals(2, response.getData().size());

        // Assert
        TimeSlotDTO timeSlotDTO1 = response.getData().get(0);
        assertEquals(1L, timeSlotDTO1.getId());
        assertEquals("09:00", timeSlotDTO1.getTimeStart());
        assertEquals("10:00", timeSlotDTO1.getTimeEnd());
        assertEquals("Occupied", timeSlotDTO1.getStatus());

        TimeSlotDTO timeSlotDTO2 = response.getData().get(1);
        assertEquals(2L, timeSlotDTO2.getId());
        assertEquals("10:00", timeSlotDTO2.getTimeStart());
        assertEquals("11:00", timeSlotDTO2.getTimeEnd());
        assertNotNull(timeSlotDTO2.getStatus());

        verify(mentorRepository).existsById(mentorId);
        verify(fixedTimeSlotRepository).findByMentorId(mentorId);
        verify(bookingRepository).findByMentorIdAndBookingDate(mentorId, date);
    }

    @Test
    void getAllTimeSlotsOfMentor_shouldThrowResourceNotFoundException_whenMentorDoesNotExist() throws ResourceNotFoundException, UnexpectedServerException {
        // Arrange
        Long mentorId = 999L;
        LocalDate date = LocalDate.now();

        when(mentorRepository.existsById(mentorId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            fixedTimeSlotService.getAllTimeSlotsOfMentor(mentorId, date);
        });

        // Assert
        assertEquals(NO_MENTORS_AVAILABLE, exception.getMessage());


        verify(mentorRepository).existsById(mentorId);
        verify(fixedTimeSlotRepository, never()).findByMentorId(mentorId);
        verify(bookingRepository, never()).findByMentorIdAndBookingDate(mentorId, date);
    }

    @Test
    void getAllTimeSlotsOfMentor_shouldThrowResourceNotFoundException_whenNoTimeSlotsAvailableForMentor() throws ResourceNotFoundException, UnexpectedServerException {
        Long mentorId = 1L;
        LocalDate date = LocalDate.now();

        when(mentorRepository.existsById(mentorId)).thenReturn(true);
        when(fixedTimeSlotRepository.findByMentorId(mentorId)).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            fixedTimeSlotService.getAllTimeSlotsOfMentor(mentorId, date);
        });

        assertEquals(NO_TIME_SLOTS_AVAILABLE_FOR_MENTOR, exception.getMessage());

        verify(mentorRepository).existsById(mentorId);
        verify(fixedTimeSlotRepository).findByMentorId(mentorId);
        verify(bookingRepository, never()).findByMentorIdAndBookingDate(mentorId, date);
    }

    @Test
    void getAllTimeSlotsOfMentor_shouldThrowUnexpectedServerException_whenServerErrorOccurs() throws ResourceNotFoundException, UnexpectedServerException {
        Long mentorId = 1L;
        LocalDate date = LocalDate.now();

        when(mentorRepository.existsById(mentorId)).thenReturn(true);
        when(fixedTimeSlotRepository.findByMentorId(mentorId)).thenThrow(new RuntimeException("Database error"));

        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
            fixedTimeSlotService.getAllTimeSlotsOfMentor(mentorId, date);
        });

        assertTrue(exception.getMessage().contains(ERROR_FETCHING_TIME_SLOTS_FOR_MENTOR));

        verify(mentorRepository).existsById(mentorId);
        verify(fixedTimeSlotRepository).findByMentorId(mentorId);
        verify(bookingRepository, never()).findByMentorIdAndBookingDate(mentorId, date);
    }




}
