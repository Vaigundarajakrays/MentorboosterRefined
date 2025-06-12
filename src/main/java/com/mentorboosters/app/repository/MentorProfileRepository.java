package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailOrPhone(String email, String phone);

    Optional<MentorProfile> findByEmail(String emailId);
}
