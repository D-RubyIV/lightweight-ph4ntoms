package com.ph4ntoms.authenticate.request.kafka;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SendEmailRequest {
    @NotNull
    @NotBlank
    private String to;
    private String from;
    @NotNull
    @NotBlank
    private String subject;
    @NotNull
    @NotBlank
    private String body;
    private String cc;
    private String bcc;
    private String replyTo;
    private String attachment;
    private String templateId;
    private String templateVariables;
}
