package com.mentorboosters.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Skill extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subHeading;

    @ElementCollection
    @CollectionTable(name = "skill_points", joinColumns = @JoinColumn(name = "skill_id"))
    @Column(name = "point")
    private List<String> points;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    @JsonIgnore
    private MentorProfile mentorProfile;

    public Skill(String subHeading, List<String> points) {
        this.subHeading = subHeading;
        this.points = points;
    }

}
