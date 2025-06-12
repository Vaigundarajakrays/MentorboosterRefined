package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByEmailId(String emailId);

    Users findByEmailId(String email);

    void deleteByEmailId(String email);

//    @Query("SELECT u.name from Users u where u.id = :recipientId")
//    String findNameById(@Param("recipientId") Long recipientId);

//    boolean existsByEmailIdOrPhoneNumber(String emailId, String phoneNumber);
}
