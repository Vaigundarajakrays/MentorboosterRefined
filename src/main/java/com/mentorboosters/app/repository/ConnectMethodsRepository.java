package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.ConnectMethods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectMethodsRepository extends JpaRepository<ConnectMethods, Long> {
    boolean existsByName(String name);
}
