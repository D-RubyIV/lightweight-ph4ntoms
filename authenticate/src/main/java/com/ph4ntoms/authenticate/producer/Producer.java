package com.ph4ntoms.authenticate.producer;

import com.ph4ntoms.authenticate.request.kafka.SendEmailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Validated
public class Producer {
    private final KafkaTemplate<String, SendEmailRequest> kafkaTemplate;

    public void sendMessage(@Valid SendEmailRequest request) {
        Message<SendEmailRequest> message = MessageBuilder
                .withPayload(request)
                .setHeader(TOPIC, "authenticate-message-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
