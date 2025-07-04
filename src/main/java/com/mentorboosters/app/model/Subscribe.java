package com.mentorboosters.app.model;

import com.mentorboosters.app.enumUtil.Role;
import com.mentorboosters.app.enumUtil.SubscribeStatus;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@EqualsAndHashCode(callSuper = true)
public class Subscribe extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscribeStatus status;
}
