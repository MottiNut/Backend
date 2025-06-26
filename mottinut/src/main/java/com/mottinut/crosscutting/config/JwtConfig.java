package com.mottinut.crosscutting.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret = "your-secret-key-here-make-it-longer-for-production";
    private long expiration = 86400000; // 24 horas en milisegundos

}
