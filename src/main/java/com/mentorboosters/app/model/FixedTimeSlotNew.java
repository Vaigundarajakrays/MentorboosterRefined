package com.mentorboosters.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@ToString(exclude = "mentor")
@Table(name = "fixed_time_slots_new")
@EqualsAndHashCode(callSuper = true)
public class FixedTimeSlotNew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant timeStart;  //UTC

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    @JsonIgnore
    private MentorProfile mentor;
}
