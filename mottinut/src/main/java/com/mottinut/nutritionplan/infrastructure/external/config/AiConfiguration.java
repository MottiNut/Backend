package com.mottinut.nutritionplan.infrastructure.external.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.nutritionplan.domain.services.AiPlanGeneratorService;
import com.mottinut.nutritionplan.domain.services.SpringBootAiPlanGeneratorService;
import com.mottinut.nutritionplan.infrastructure.external.ai.OpenAIClient;
import com.mottinut.patient.domain.repositories.MedicalHistoryRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AiConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Configurar timeout
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(100000); // 30 segundos
        factory.setReadTimeout(400000);    // 60 segundos
        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    // Bean condicional - usa SpringBootAi si no hay FastAPI configurado
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "spring", matchIfMissing = true)
    public AiPlanGeneratorService springBootAiPlanGeneratorService(
            OpenAIClient openAIClient, UserService userService, MedicalHistoryRepository medicalHistoryRepository) {
        return new SpringBootAiPlanGeneratorService(openAIClient, userService, medicalHistoryRepository);
    }


}