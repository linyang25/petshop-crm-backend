//package com.petshop.crmbackend.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
//import software.amazon.awssdk.services.s3.presigner.model.GetObjectRequest;
//
//import java.time.Duration;
//
//@Service
//public class S3PresignService {
//
//    private final S3Presigner presigner;
//    @Value("${aws.s3.bucket}")
//    private String bucket;
//
//    public S3PresignService(S3Presigner presigner) {
//        this.presigner = presigner;
//    }
//
//    /**
//     * 根据 key（即在 S3 中的完整路径）生成一个带签名的 URL，
//     * 有效期 timeoutSeconds 秒
//     */
//    public String presignGetUrl(String key, long timeoutSeconds) {
//        GetObjectRequest getObject = GetObjectRequest.builder()
//                .bucket(bucket)
//                .key(key)
//                .build();
//
//        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .getObjectRequest(getObject)
//                .signatureDuration(Duration.ofSeconds(timeoutSeconds))
//                .build();
//
//        return presigner.presignGetObject(presignRequest)
//                .url()
//                .toString();
//    }
//}
