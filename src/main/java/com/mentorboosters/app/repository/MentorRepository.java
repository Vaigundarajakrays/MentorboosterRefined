package com.mentorboosters.app.repository;

import com.mentorboosters.app.dto.SearchMentorsDTO;
import com.mentorboosters.app.dto.TopMentorsDTO;
import com.mentorboosters.app.dto.TopRatedMentorsDTO;
import com.mentorboosters.app.dto.VerifiedMentorDTO;
import com.mentorboosters.app.model.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    boolean existsByEmail(String email);

    @Query(value = """
        SELECT m.name AS name,
               m.gender AS gender, 
               m.avatar_url AS avatarUrl,  
               m.number_of_mentoree AS numberOfMentoree,
               STRING_AGG(c.name, ', ') AS categoryNames
        FROM mentors m
        JOIN mentor_categories mc ON m.id = mc.mentor_id
        JOIN categories c ON mc.category_id = c.id
        WHERE m.verified = true
        GROUP BY m.id, m.name, m.gender, m.avatar_url, m.number_of_mentoree
        ORDER BY m.number_of_mentoree DESC
    """, nativeQuery = true)
    List<VerifiedMentorDTO> findVerifiedMentors();

    @Query(value = """
            SELECT m.name AS name,
                   m.gender AS gender, 
                   m.avatar_url AS avatarUrl,
                   m.number_of_mentoree AS numberOfMentoree,
                   m.rate AS rate,
                   STRING_AGG(c.name, ', ') AS categoryNames
            FROM mentors m
            JOIN mentor_categories mc ON m.id = mc.mentor_id
            JOIN categories c ON mc.category_id = c.id
            GROUP BY m.id, m.name, m.gender, m.avatar_url, m.number_of_mentoree, m.rate
            ORDER BY m.rate DESC      
    """, nativeQuery = true)
    List<TopRatedMentorsDTO> findTopRatedMentors();

    @Query(value = """
            SELECT m.id,
                   m.name AS name,
                   m.gender AS gender, 
                   m.avatar_url AS avatarUrl,
                   m.number_of_mentoree AS numberOfMentoree,
                   m.rate AS rate,
                   STRING_AGG(c.name, ', ') AS categoryNames
            FROM mentors m
            JOIN mentor_categories mc ON m.id = mc.mentor_id
            JOIN categories c ON mc.category_id = c.id
            GROUP BY m.id, m.name, m.gender, m.avatar_url, m.number_of_mentoree, m.rate
            ORDER BY m.number_of_mentoree DESC  
            """, nativeQuery = true)
    List<TopMentorsDTO> findTopMentors();

    @Query(value = """
            SELECT
                    m.id AS id,
                    m.name AS name,
                    m.number_of_mentoree AS numberOfMentoree,
                    m.rate AS rate,
                    m.role AS role,
                    m.gender AS gender,
                    m.avatar_url AS avatarUrl,
                    STRING_AGG(c.name, ', ') AS categoryNames
                FROM
                    mentors m
                JOIN
                    mentor_categories mc ON m.id = mc.mentor_id
                JOIN
                    categories c ON mc.category_id = c.id
                WHERE
                    EXISTS (
                        SELECT 1 FROM unnest(CAST(:items AS text[])) AS kw
                                WHERE
                                    m.name ILIKE CONCAT('%', kw, '%')
                                    OR m.role ILIKE CONCAT('%', kw, '%')
                                    OR c.name ILIKE CONCAT('%', kw, '%')
                    )
                GROUP BY
                    m.id
                ORDER BY
                    m.number_of_mentoree DESC;
            """, nativeQuery = true)
    List<SearchMentorsDTO> searchMentors(@Param("items") String[] items);  // cast tells postgre treat items as array of texts or strings,
                                                                           // unnest convert the array of string into virtual table called kw with each string as row


}
