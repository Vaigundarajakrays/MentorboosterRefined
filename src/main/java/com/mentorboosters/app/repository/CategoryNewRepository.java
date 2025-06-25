package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.CategoryNew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryNewRepository extends JpaRepository<CategoryNew, Long> {
    Optional<CategoryNew> findByName(String name);
}
