package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.TimeSlotDTO;
import com.mentorboosters.app.enumUtil.PaymentStatus;
import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.model.FixedTimeSlotNew;
import com.mentorboosters.app.repository.BookingRepository;
import com.mentorboosters.app.repository.FixedTimeSlotNewRepository;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mentorboosters.app.util.Constant.*;
import static com.mentorboosters.app.util.Constant.ERROR_FETCHING_TIME_SLOTS_FOR_MENTOR;

@Service
@RequiredArgsConstructor
public class FixedTimeSlotNewService {

    private final FixedTimeSlotNewRepository fixedTimeSlotNewRepository;
    private final BookingRepository bookingRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    public CommonResponse<List<TimeSlotDTO>> getTimeSlotsOfMentor(Long mentorId, Long menteeId, String localDate) throws ResourceNotFoundException, UnexpectedServerException {

        if (!mentorProfileRepository.existsById(mentorId)) {
            throw new ResourceNotFoundException(NO_MENTORS_AVAILABLE);
        }

        String timezone = menteeProfileRepository.findById(menteeId)
                .orElseThrow(() -> new ResourceNotFoundException( MENTEE_NOT_FOUND_WITH_ID + menteeId))
                .getTimeZone();

        // Convert string timezone to ZoneId
        final ZoneId menteeTimezone;
        try {
            menteeTimezone = ZoneId.of(timezone);
        } catch (DateTimeException e){
            throw new InvalidFieldValueException("Invalid Timezone");
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Converting string date to LocalDate
        LocalDate date;
        try {
            date = LocalDate.parse(localDate, dateFormatter);
        } catch (DateTimeParseException e){
            throw new InvalidFieldValueException("Date must be yyyy-mm-dd format");
        }


        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // Get current time in mentee timezone
            ZonedDateTime now = ZonedDateTime.now(menteeTimezone);

            // 1. Fetch all fixed time slots for the mentor (stored as UTC Instants)
            List<FixedTimeSlotNew> slots = fixedTimeSlotNewRepository.findByMentorId(mentorId);

            if (slots.isEmpty()) {
                throw new ResourceNotFoundException(NO_TIME_SLOTS_AVAILABLE_FOR_MENTOR);
            }

            // 2. Fetch bookings only for that date in user's timezone
            ZonedDateTime dayStartZoned = date.atStartOfDay(menteeTimezone);
            ZonedDateTime dayEndZoned = dayStartZoned.plusDays(1);

            Instant utcStart = dayStartZoned.toInstant();
            Instant utcEnd = dayEndZoned.toInstant();

            List<Booking> bookings = bookingRepository.findByMentorIdAndSessionStartTimeBetweenAndPaymentStatus(
                    mentorId, utcStart, utcEnd, PaymentStatus.COMPLETED
            );

            Set<Long> bookedSlotIds = bookings.stream()
                    .map(Booking::getTimeSlotId)
                    .collect(Collectors.toSet());

            List<TimeSlotDTO> timeSlotDTOS = slots.stream()
                    .map(slot -> {
                        // converting the mentor slot's start time to mentee local time
                        // Since mentor is available daily at that time, we cant compare with mentee and mentor Instant
                        LocalTime slotTime = slot.getTimeStart()
                                .atZone(ZoneOffset.UTC)
                                .withZoneSameInstant(menteeTimezone)
                                .toLocalTime();

                        // Apply that time to the requested date
                        ZonedDateTime userZonedTime = date.atTime(slotTime).atZone(menteeTimezone);

                        String status;
                        if (bookedSlotIds.contains(slot.getId())) {
                            status = OCCUPIED;
                        } else if (userZonedTime.isBefore(now)) {
                            status = NOT_AVAILABLE;
                        } else {
                            status =AVAILABLE;
                        }

                        return TimeSlotDTO.builder()
                                .id(slot.getId())
                                .timeStart(slotTime.format(formatter))
                                .timeEnd(slotTime.plusHours(1).format(formatter))
                                .status(status)
                                .build();
                    })
                    .toList();


            return CommonResponse.<List<TimeSlotDTO>>builder()
                    .message(LOADED_ALL_TIME_SLOTS_FOR_MENTORS)
                    .status(STATUS_TRUE)
                    .data(timeSlotDTOS)
                    .statusCode(SUCCESS_CODE)
                    .build();
        }
        catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedServerException(ERROR_FETCHING_TIME_SLOTS_FOR_MENTOR + e.getMessage());
        }
    }

}
