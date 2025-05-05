package com.ph4ntoms.authenticate.coverter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Long> {
    @Override
    public Long convertToDatabaseColumn(LocalDateTime attribute) {
        return (attribute == null) ? null : attribute.toInstant(ZoneOffset.UTC).toEpochMilli(); // Sử dụng epoch milli
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long dbData) {
        return (dbData == null) ? null : Instant.ofEpochMilli(dbData).atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}