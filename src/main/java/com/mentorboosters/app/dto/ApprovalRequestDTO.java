package com.mentorboosters.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalRequestDTO {

    @NotBlank(message = "Status must not be blank (APPROVED or REJECTED)")
    private String status;

}
