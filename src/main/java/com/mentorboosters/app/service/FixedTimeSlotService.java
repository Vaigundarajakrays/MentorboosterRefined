//package com.mentorboosters.app.service;
//
//import com.mentorboosters.app.dto.TimeSlotDTO;
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.Booking;
//import com.mentorboosters.app.model.FixedTimeSlot;
//import com.mentorboosters.app.repository.BookingRepository;
//import com.mentorboosters.app.repository.FixedTimeSlotRepository;
//import com.mentorboosters.app.repository.MentorRepository;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.util.Constant;
//import org.springframework.stereotype.Service;
//
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static com.mentorboosters.app.util.Constant.*;
//
//@Service
//public class FixedTimeSlotService {
//
//    private final FixedTimeSlotRepository fixedTimeSlotRepository;
//    private final BookingRepository bookingRepository;
//    private final MentorRepository mentorRepository;
//
//    public FixedTimeSlotService(FixedTimeSlotRepository fixedTimeSlotRepository, BookingRepository bookingRepository, MentorRepository mentorRepository){
//        this.fixedTimeSlotRepository=fixedTimeSlotRepository;
//        this.bookingRepository=bookingRepository;
//        this.mentorRepository=mentorRepository;
//    }
//
//    public CommonResponse<List<TimeSlotDTO>> getAllTimeSlotsOfMentor(Long mentorId, LocalDate date, String timezone) throws ResourceNotFoundException, UnexpectedServerException {
//
//        if(!(mentorRepository.existsById(mentorId))){
//            throw new ResourceNotFoundException(NO_MENTORS_AVAILABLE);
//        }
//
//        try {
//
//            ZoneId zoneId;
//            try {
//                zoneId = ZoneId.of(timezone);
//            } catch (DateTimeException e) {
//                zoneId = ZoneOffset.UTC; // fallback to UTC if timezone is invalid
//            }
//
//            ZonedDateTime now = ZonedDateTime.now(zoneId);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//
//            List<FixedTimeSlot> slots = fixedTimeSlotRepository.findByMentorId(mentorId);
//
//            if (slots.isEmpty()) {
//                throw new ResourceNotFoundException(NO_TIME_SLOTS_AVAILABLE_FOR_MENTOR);
//            }
//
//            //If returns empty list, then the SET also become empty, so status never becomes occupied
//            List<Booking> bookings = bookingRepository.findByMentorIdAndBookingDateAndPaymentStatus(mentorId, date, "completed");
//
//            // .map(booking -> booking.getTimeSlotId()) IS WRITTEN AS .map(Booking::getTimeSlotId)
//            Set<Long> bookedSlotIds = bookings.stream().map(Booking::getTimeSlotId).collect(Collectors.toSet());
//
//            ZoneId finalZoneId = zoneId;
//            List<TimeSlotDTO> timeSlotDTOS = slots.stream().map(slot -> {
//
//                LocalTime startTime = slot.getTimeStart();
//                LocalTime endTime = slot.getTimeEnd();
//
//                // Combine date + time + zone to form a ZonedDateTime for slot start
//                ZonedDateTime slotStartTime = ZonedDateTime.of(date, startTime, finalZoneId);
//
//                String status;
//
//                // Using Set for fast O(1) and List takes O(n) lookup and avoiding duplicate slot IDs by mistake
//                if (bookedSlotIds.contains(slot.getId())) {
//                    status = "Occupied";
//                } else if (date.isBefore(now.toLocalDate()) || date.isEqual(now.toLocalDate()) && slotStartTime.isBefore(now)) {
//                    status = "Not available";
//                } else {
//                    status = "Available";
//                }
//
//                return TimeSlotDTO.builder()
//                        .id(slot.getId())
//                        .timeStart(startTime.format(formatter))
//                        .timeEnd(endTime.format(formatter))
//                        .status(status)
//                        .build();
//            }).toList();
//
//            return CommonResponse.<List<TimeSlotDTO>>builder()
//                    .message(LOADED_ALL_TIME_SLOTS_FOR_MENTORS)
//                    .status(STATUS_TRUE)
//                    .data(timeSlotDTOS)
//                    .statusCode(SUCCESS_CODE)
//                    .build();
//
//        }
//
//        catch (ResourceNotFoundException e){
//            throw e;
//        }
//
//        catch (Exception e) {
//            throw new UnexpectedServerException(ERROR_FETCHING_TIME_SLOTS_FOR_MENTOR + e.getMessage());
//        }
//    }
//}
