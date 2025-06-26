package com.mentorboosters.app.repository;

import com.mentorboosters.app.dto.MentorProfileDTO;
import com.mentorboosters.app.enumUtil.AccountStatus;
import com.mentorboosters.app.enumUtil.ApprovalStatus;
import com.mentorboosters.app.model.MenteeProfile;
import com.mentorboosters.app.model.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailOrPhone(String email, String phone);

    Optional<MentorProfile> findByEmail(String emailId);

    Long countByApprovalStatus(ApprovalStatus approvalStatus);

    List<MentorProfile> findByApprovalStatus(ApprovalStatus approvalStatus);

    List<MentorProfile> findAllByApprovalStatusAndAccountStatus(ApprovalStatus approvalStatus, AccountStatus accountStatus);
}
