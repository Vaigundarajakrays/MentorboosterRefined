package com.mentorboosters.app.service;

import com.mentorboosters.app.dto.ReviewDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Mentor;
import com.mentorboosters.app.model.Review;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.MentorRepository;
import com.mentorboosters.app.repository.ReviewRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;


import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class ReviewService {

    private final MentorRepository mentorRepository;
    private final UsersRepository usersRepository;
    private final ReviewRepository reviewRepository;

    public ReviewService(MentorRepository mentorRepository, UsersRepository usersRepository, ReviewRepository reviewRepository){
        this.mentorRepository=mentorRepository;
        this.usersRepository=usersRepository;
        this.reviewRepository=reviewRepository;
    }

    // wanna add code to save duplicate reviews
//    public CommonResponse<Review> saveReview(ReviewDTO reviewDTO) throws ResourceNotFoundException, UnexpectedServerException {
//
//        Mentor mentor = mentorRepository.findById(reviewDTO.getMentorId()).orElseThrow(()-> new ResourceNotFoundException(MENTOR_NOT_FOUND_WITH_ID + reviewDTO.getMentorId()));
//
//        Users user = usersRepository.findById(reviewDTO.getUserId()).orElseThrow(()-> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + reviewDTO.getUserId()));
//
//        try {
//
//            Review review = Review.builder()
//                    .message(reviewDTO.getMessage())
//                    .rating(reviewDTO.getRating())
//                    .createdById(reviewDTO.getUserId())
//                    .userName(user.getName())
//                    .mentor(mentor)
//                    .build();
//
//            Review savedReview = reviewRepository.save(review);
//
//            //Fetch all reviews for this mentor
//            List<Review> reviews = reviewRepository.findByMentorId(mentor.getId());
//
//            //Calculate the new average rating
//            double averageRating = reviews.stream()
//                    .mapToDouble(Review::getRating)
//                    .average()
//                    .orElse(0.0); // default to 0 if no reviews (shouldn’t happen here)
//
//            //Round to 1 decimal place, 4.25 is converted to 4.3(rounding mode.half up)
//            BigDecimal roundedRating = new BigDecimal(averageRating).setScale(1, RoundingMode.HALF_UP);
//
//            //Update mentor's rating and save
//            mentor.setRate(roundedRating.doubleValue());
//            mentorRepository.save(mentor);
//
//            return CommonResponse.<Review>builder()
//                    .message(SUCCESSFULLY_ADDED)
//                    .status(STATUS_TRUE)
//                    .data(savedReview)
//                    .statusCode(SUCCESS_CODE)
//                    .build();
//
//        } catch (Exception e){
//            throw new UnexpectedServerException(ERROR_ADDING_REVIEW + e.getMessage());
//        }
//    }


}
