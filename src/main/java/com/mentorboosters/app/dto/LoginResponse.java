package com.mentorboosters.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mentorboosters.app.enumUtil.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponse {

    private String token;

    private Long userId;

    private String name;

    private String emailId;

    private String phoneNumber;

    private String description;

    private List<String> goals;

    private Role role;

}
