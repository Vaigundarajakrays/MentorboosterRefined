package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByMenteeId(Long menteeId);
}
