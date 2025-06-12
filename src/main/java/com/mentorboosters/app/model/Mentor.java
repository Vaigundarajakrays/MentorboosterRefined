package com.mentorboosters.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@ToString(exclude = {"certificates", "experiences", "timeSlots", "reviews"})
@Table(name = "mentors")
@EqualsAndHashCode(callSuper = true)
public class Mentor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String avatarUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Double freePrice;

    @Column(nullable = false)
    private String freeUnit;

    @Column(nullable = false)
    private Boolean verified;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private Integer numberOfMentoree;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FixedTimeSlot> timeSlots;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    //whenever the Mentor is saved, Hibernate will also save any unsaved Category instances associated with it.
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "mentor_categories",
            joinColumns = @JoinColumn(name = "mentor_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

}
