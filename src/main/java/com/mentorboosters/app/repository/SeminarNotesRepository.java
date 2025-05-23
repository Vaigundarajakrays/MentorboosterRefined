package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.SeminarNotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeminarNotesRepository extends JpaRepository<SeminarNotes, Long> {

    boolean existsByUserIdAndTitle(Long userId, String title);

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);

    SeminarNotes findByUserId(Long userId);
}
