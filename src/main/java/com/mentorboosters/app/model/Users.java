package com.mentorboosters.app.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mentorboosters.app.enumUtil.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
public class Users extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String emailId;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String description;

    @ElementCollection
    private List<String> goals;

    //To store the enum in db as string, and if we removed EnumType.STRING, it store indices value in db like 0, 1,2
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
//    }


}
