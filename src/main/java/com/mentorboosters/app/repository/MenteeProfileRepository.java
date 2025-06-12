package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.MenteeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenteeProfileRepository extends JpaRepository<MenteeProfile, Long> {
    boolean existsByEmailOrPhone(String email, String phone);

    boolean existsByEmail(String email);

    Optional<MenteeProfile> findByEmail(String emailId);
}
