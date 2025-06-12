package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.FixedTimeSlotNew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedTimeSlotNewRepository extends JpaRepository<FixedTimeSlotNew, Long> {
    List<FixedTimeSlotNew> findByMentorId(Long mentorId);
}
