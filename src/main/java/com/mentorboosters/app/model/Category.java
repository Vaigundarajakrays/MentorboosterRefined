package com.mentorboosters.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@ToString(exclude = "mentors")
@Table(name = "categories")
@EqualsAndHashCode(callSuper = true) // this class fields and base or super class fields also have equal and hash method
public class Category extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Mentor> mentors;

}
