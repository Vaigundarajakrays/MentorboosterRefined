package com.mentorboosters.app.controller;

import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload/{type}")
    public CommonResponse<String> uploadFileToFolder(@PathVariable("type") String type, @RequestParam("file") MultipartFile file) throws UnexpectedServerException {

        if (!List.of("mentor-images", "mentor-resumes", "mentee-images").contains(type)) {
            throw new InvalidFieldValueException("Invalid upload folder type.");
        }

        return s3Service.uploadFile(file, type);
    }

}

