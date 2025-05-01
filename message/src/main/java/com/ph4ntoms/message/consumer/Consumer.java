package com.ph4ntoms.message.consumer;

import com.ph4ntoms.message.request.SendEmailRequest;
import com.ph4ntoms.message.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Consumer {
    private final EmailService emailService;

    @KafkaListener(topics = "authenticate-message-topic", groupId = "authenticate-group")
    public void consume(SendEmailRequest sendEmailRequest) {
        System.out.println("Received: " + sendEmailRequest);
        emailService.sendEmail(sendEmailRequest);
    }
}
