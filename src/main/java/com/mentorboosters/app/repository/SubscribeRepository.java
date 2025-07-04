package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    boolean existsByEmail(String email);

    Subscribe findByEmail(String email);
}
