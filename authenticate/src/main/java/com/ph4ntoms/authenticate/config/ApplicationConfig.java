package com.ph4ntoms.authenticate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareConfig();
    }
}
