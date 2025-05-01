package com.ph4ntoms.message.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendEmailRequest {
    private String to;
    private String from;
    private String subject;
    private String body;
    private String cc;
    private String bcc;
    private String replyTo;
    private String attachment;
    private String templateId;
    private String templateVariables;
}