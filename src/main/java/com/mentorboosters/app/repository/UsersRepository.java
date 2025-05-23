package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByEmailId(String emailId);

    boolean existsByUserName(String userName);

    @Query("SELECT u.userName from Users u where u.id = :recipientId")
    String findUserNameById(@Param("recipientId") Long recipientId);

    Users findByEmailId(String email);

    Optional<Object> findByUserName(String username);

    void deleteByEmailId(String email);
}
