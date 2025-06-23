package com.mentorboosters.app.response;

import com.mentorboosters.app.dto.MentorProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommonResponse<T> {

    private Boolean status;
    private String message;
    private T data;
    private Integer statusCode;
    private String error;

}
