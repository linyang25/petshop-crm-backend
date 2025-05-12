package com.petshop.crmbackend.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {

    private final SesClient ses;
    private final String fromAddress;

    public EmailService(
            @Value("${aws.region}") String region,
            @Value("${aws.credentials.access-key-id}") String accessKey,
            @Value("${aws.credentials.secret-access-key}") String secretKey,
            @Value("${aws.ses.from-address}") String fromAddress
    ) {
        System.out.println(">>> 当前 SES 发件人(fromAddress) = " + fromAddress);
        this.fromAddress = fromAddress;
        this.ses = SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * 发送一封纯文本预约提醒邮件
     */
    public void sendAppointmentReminder(String toAddress,
                                        String subject,
                                        String bodyText) {
        Destination destination = Destination.builder()
                .toAddresses(toAddress)
                .build();

        Content subjectContent = Content.builder()
                .data(subject)
                .build();

        Content bodyContent = Content.builder()
                .data(bodyText)
                .build();

        Body body = Body.builder()
                .text(bodyContent)
                .build();

        Message message = Message.builder()
                .subject(subjectContent)
                .body(body)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .source(fromAddress)
                .destination(destination)
                .message(message)
                .build();

        ses.sendEmail(request);
    }
}
