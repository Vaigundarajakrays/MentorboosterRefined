package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.ReviewDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Review;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mentorboosters/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){this.reviewService=reviewService;}

    @PostMapping("/saveReview")
    public CommonResponse<Review> saveReview(@RequestBody ReviewDTO reviewDTO) throws UnexpectedServerException, ResourceNotFoundException {
        return reviewService.saveReview(reviewDTO);
    }
}
