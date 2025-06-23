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
    private Role role;
    private String name;
    private Long id;
    private String timezone;
    private String profileUrl;

}
