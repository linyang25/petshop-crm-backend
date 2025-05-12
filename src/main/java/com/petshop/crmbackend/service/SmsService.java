//package com.petshop.crmbackend.service;
//
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.sns.SnsClient;
//import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
//import software.amazon.awssdk.services.sns.model.PublishRequest;
//import software.amazon.awssdk.services.sns.model.PublishResponse;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class SmsService {
//
//    private final SnsClient snsClient;
//
//    public SmsService() {
//        this.snsClient = SnsClient.builder()
//                .region(Region.AP_SOUTHEAST_1) // 替换为您使用的区域
//                .credentialsProvider(ProfileCredentialsProvider.create())
//                .build();
//    }
//
//    public void sendSms(String phoneNumber, String message) {
//        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
//        smsAttributes.put("AWS.SNS.SMS.SMSType", MessageAttributeValue.builder()
//                .stringValue("Transactional") // 可选值：Promotional, Transactional
//                .dataType("String")
//                .build());
//
//        PublishRequest request = PublishRequest.builder()
//                .message(message)
//                .phoneNumber(phoneNumber)
//                .messageAttributes(smsAttributes)
//                .build();
//
//        PublishResponse result = snsClient.publish(request);
//        System.out.println("消息已发送，消息ID: " + result.messageId());
//    }
//}