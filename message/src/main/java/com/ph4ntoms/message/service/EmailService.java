package com.ph4ntoms.message.service;

import com.ph4ntoms.message.request.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(SendEmailRequest request) {
        validateRequest(request);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set basic email properties
            helper.setTo(request.getTo());
            helper.setFrom(request.getFrom());
            helper.setSubject(request.getSubject());

            // Handle CC and BCC
            if (StringUtils.hasText(request.getCc())) {
                helper.setCc(request.getCc().split(","));
            }
            if (StringUtils.hasText(request.getBcc())) {
                helper.setBcc(request.getBcc().split(","));
            }
            if (StringUtils.hasText(request.getReplyTo())) {
                helper.setReplyTo(request.getReplyTo());
            }

            // Handle template if provided
            if (StringUtils.hasText(request.getTemplateId())) {
                Context context = new Context();
                if (StringUtils.hasText(request.getTemplateVariables())) {
                    Map<String, Object> variables = parseTemplateVariables(request.getTemplateVariables());
                    context.setVariables(variables);
                }
                String htmlContent = templateEngine.process(request.getTemplateId(), context);
                helper.setText(htmlContent, true);
            } else {
                helper.setText(request.getBody(), true); // Set as HTML by default
            }

            // Handle attachment if provided
            if (StringUtils.hasText(request.getAttachment())) {
                File attachment = new File(request.getAttachment());
                if (attachment.exists()) {
                    helper.addAttachment(attachment.getName(), attachment);
                }
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private void validateRequest(SendEmailRequest request) {
        if (!StringUtils.hasText(request.getTo())) {
            throw new IllegalArgumentException("Recipient email address is required");
        }
        if (!StringUtils.hasText(request.getFrom())) {
            throw new IllegalArgumentException("Sender email address is required");
        }
        if (!StringUtils.hasText(request.getSubject())) {
            throw new IllegalArgumentException("Email subject is required");
        }
        if (!StringUtils.hasText(request.getBody()) && !StringUtils.hasText(request.getTemplateId())) {
            throw new IllegalArgumentException("Either email body or template ID must be provided");
        }
    }

    private Map<String, Object> parseTemplateVariables(String templateVariables) {
        return Arrays.stream(templateVariables.split(","))
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(
                        pair -> pair[0].trim(),
                        pair -> pair[1].trim()
                ));
    }
}