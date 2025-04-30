package com.ph4ntoms.message.service;

import com.ph4ntoms.message.domain.SimpleMessageObject;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(SimpleMessageObject messageObject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(messageObject.getTo());
        message.setSubject(messageObject.getSubject());
        message.setText(messageObject.getContent());
        mailSender.send(message);
    }
}