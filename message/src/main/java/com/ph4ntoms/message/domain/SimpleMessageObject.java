package com.ph4ntoms.message.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class SimpleMessageObject {
    private String to;
    private String content;
    private String subject;
}