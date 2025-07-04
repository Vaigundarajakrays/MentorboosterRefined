package com.mentorboosters.app.service;

import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Feedback;
import com.mentorboosters.app.repository.FeedbackRepository;
import com.mentorboosters.app.repository.MenteeProfileRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    public CommonResponse<String> saveFeedback(Feedback feedback) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            if(feedback.getMenteeId()==null){
                throw new InvalidFieldValueException("Mentee id should not be null");
            }

            if(!menteeProfileRepository.existsById(feedback.getMenteeId())){
                throw new ResourceNotFoundException("Mentee not found with id: " + feedback.getMenteeId());
            }

            if(feedbackRepository.existsByMenteeId(feedback.getMenteeId())){
                throw new ResourceAlreadyExistsException("You already gave feedback");
            }

            feedbackRepository.save(feedback);

            return CommonResponse.<String>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("Thanks for your Feedback")
                    .build();

        } catch (ResourceNotFoundException | InvalidFieldValueException | ResourceAlreadyExistsException e){
            throw e;
        } catch (Exception e){
            throw new UnexpectedServerException("Error while saving feedback: " + e.getMessage());
        }


    }
}
