package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.FixedTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedTimeSlotRepository extends JpaRepository<FixedTimeSlot, Long> {

    List<FixedTimeSlot> findByMentorId(Long mentorId);
}
