package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience,Long> {
}
