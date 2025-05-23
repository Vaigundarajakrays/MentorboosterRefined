package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.ReviewDTO;
import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Mentor;
import com.mentorboosters.app.model.Review;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.MentorRepository;
import com.mentorboosters.app.repository.ReviewRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mentorboosters.app.util.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    MentorRepository mentorRepository;

    @Mock
    UsersRepository usersRepository;

    @Mock
    ReviewRepository reviewRepository;

    @InjectMocks
    ReviewService reviewService;

    @Test
    void saveReviewShouldSaveSuccessfully() throws ResourceNotFoundException, UnexpectedServerException {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(4L)
                .message("Great mentor!")
                .mentorId(1L)
                .userId(2L)
                .build();

        Mentor mentor1 = Mentor.builder()
                .id(1L)
                .name("Joe1")
                .email("joe1@gmail.com")
                .gender("Male")
                .avatarUrl("https://example.com/avatar.jpg")
                .bio("Experienced mentor")
                .role("Software Engineer")
                .freePrice(50.0)
                .freeUnit("hour")
                .verified(true)
                .rate(4.9)
                .numberOfMentoree(20)
                .build();
        mentor1.setCreatedAt(LocalDateTime.now());
        mentor1.setUpdatedAt(LocalDateTime.now());

        Users user = Users.builder()
                .id(2L)
                .userName("testuser")
                .name("Test User")
                .emailId("user@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Review expectedReview = Review.builder()
                .id(1L)
                .message("Great mentor!")
                .rating(4L)
                .createdById(2L)
                .userName("testuser")
                .mentor(mentor1)
                .build();
        expectedReview.setCreatedAt(LocalDateTime.now());
        expectedReview.setUpdatedAt(LocalDateTime.now());

        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor1));
        when(usersRepository.findById(2L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(expectedReview);

        List<Review> reviews = new ArrayList<>();
        reviews.add(expectedReview);
        when(reviewRepository.findByMentorId(1L)).thenReturn(reviews);

        CommonResponse<Review> response = reviewService.saveReview(reviewDTO);

        assertNotNull(response);
        assertEquals("Successfully Added.", response.getMessage());
        assertTrue(response.getStatus());
        assertEquals(200, response.getStatusCode());

        Review savedReview = response.getData();
        assertNotNull(savedReview);
        assertEquals(1L, savedReview.getId());
        assertEquals("Great mentor!", savedReview.getMessage());
        assertEquals(4L, savedReview.getRating());
        assertEquals(2L, savedReview.getCreatedById());
        assertEquals("testuser", savedReview.getUserName());

        assertEquals(4L, mentor1.getRate().longValue());

        verify(mentorRepository).findById(1L);
        verify(usersRepository).findById(2L);
        verify(reviewRepository).save(any(Review.class));
        verify(mentorRepository).save(mentor1);
    }

    @Test
    void saveReview_WhenMentorNotFound_ShouldThrowResourceNotFoundException() {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(4L)
                .message("Great mentor!")
                .mentorId(999L)
                .userId(2L)
                .build();

        when(mentorRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.saveReview(reviewDTO));

        assertEquals(MENTOR_NOT_FOUND_WITH_ID + "999", exception.getMessage());
        verify(mentorRepository, times(1)).findById(999L);
        verify(usersRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_WhenUserNotFound_ShouldThrowResourceNotFoundException() {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(4L)
                .message("Great mentor!")
                .mentorId(1L)
                .userId(999L)
                .build();

        Mentor mentor1 = Mentor.builder().id(1L).build();
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor1));
        when(usersRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.saveReview(reviewDTO));

        assertEquals( USER_NOT_FOUND_WITH_ID + "999", exception.getMessage());
        verify(mentorRepository, times(1)).findById(1L);
        verify(usersRepository, times(1)).findById(999L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void saveReview_WhenDatabaseErrorOccurs_ShouldThrowUnexpectedServerException() {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(4L)
                .message("Great mentor!")
                .mentorId(1L)
                .userId(2L)
                .build();

        Mentor mentor1 = Mentor.builder().id(1L).build();
        Users user = Users.builder().id(2L).userName("testuser").build();

        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor1));
        when(usersRepository.findById(2L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any())).thenThrow(new RuntimeException("Database error"));


        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class,
                () -> reviewService.saveReview(reviewDTO));

        assertEquals(ERROR_ADDING_REVIEW + "Database error", exception.getMessage());
        verify(mentorRepository, times(1)).findById(1L);
        verify(usersRepository, times(1)).findById(2L);
        verify(reviewRepository, times(1)).save(any());
    }


}
