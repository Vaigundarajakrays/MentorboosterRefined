package com.mentorboosters.app.service;

import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.ContactMessage;
import com.mentorboosters.app.repository.ContactMessageRepository;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.repository.MentorProfileRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.mentorboosters.app.util.Constant.STATUS_TRUE;
import static com.mentorboosters.app.util.Constant.SUCCESS_CODE;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    public CommonResponse<String> sendMessage(ContactMessage contactMessage) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            if (contactMessage.getName() == null || contactMessage.getMessage() == null || contactMessage.getEmail() == null || contactMessage.getSubject() == null) {
                throw new InvalidFieldValueException("Name, email, subject, message should not be null");
            }

            if (contactMessage.getMentorId() != null && !mentorProfileRepository.existsByIdAndEmail(contactMessage.getMentorId(), contactMessage.getEmail())) {
                throw new ResourceNotFoundException(String.format("Mentor not found with email: %s or mentorId: %s", contactMessage.getEmail(), contactMessage.getMentorId()));
            }

            if (contactMessage.getMenteeId() != null && !menteeProfileRepository.existsByIdAndEmail(contactMessage.getMenteeId(), contactMessage.getEmail())) {
                throw new ResourceNotFoundException(String.format("Mentee not found with email: %s or menteeId: %s", contactMessage.getEmail(), contactMessage.getMenteeId()));
            }

            contactMessageRepository.save(contactMessage);

            return CommonResponse.<String>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("Successfully sent message")
                    .build();

        } catch (InvalidFieldValueException | ResourceNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException("Error while sending message : " + e.getMessage());
        }

    }
}
