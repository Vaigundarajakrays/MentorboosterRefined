package com.mentorboosters.app.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Feedback extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long menteeId;

    private Long satisfactionScore;

    private Long valuedSupportScore;

    @Column(columnDefinition = "TEXT")
    private String message;
}
