package com.mentorboosters.app.service;

import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

import static com.mentorboosters.app.util.Constant.STATUS_TRUE;
import static com.mentorboosters.app.util.Constant.SUCCESS_CODE;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

//    Not needed because we use @required args constructor
//    public S3Service(S3Client s3Client) {
//        this.s3Client = s3Client;
//    }

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;


    public CommonResponse<String> uploadFile(MultipartFile file, String folderName) throws UnexpectedServerException {
        try {
            // Clean and build the S3 object key with folder path
            String originalFileName = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "-" + (originalFileName != null ? originalFileName : "file");

            String key = folderName + "/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;

            return CommonResponse.<String>builder()
                    .data(fileUrl)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("File successfully uploaded")
                    .build();

        } catch (Exception e) {
            throw new UnexpectedServerException("Error while uploading the file: " + e.getMessage());
        }
    }

}

