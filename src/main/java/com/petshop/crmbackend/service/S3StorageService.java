package com.petshop.crmbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;

@Service
public class S3StorageService {

    private final S3Client s3;
    private final Region region;
    private final String bucket;

    /**
     * 构造函数直接注入三个 Bean/配置
     */
    public S3StorageService(
            S3Client s3,
            Region awsRegion,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.s3 = s3;
        this.region = awsRegion;
        this.bucket = bucket;
    }

    public String upload(MultipartFile file, String petId) throws IOException {
        String key = String.format("pet-images/%s/%d-%s",
                petId, System.currentTimeMillis(), file.getOriginalFilename());

        // 上传请求
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3.putObject(req, RequestBody.fromBytes(file.getBytes()));

        // 用注入的 region.id() 拼 URL
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region.id(), key);
    }
}