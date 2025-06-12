package com.mottinut.nutritionplan.infrastructure.external.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Configurar timeouts
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(120_000); // 2 minutos (120000 ms)
        factory.setReadTimeout(240_000);    // 2 minutos (120000 ms)

        restTemplate.setRequestFactory(factory);

        // Agregar interceptor para logging (opcional)
        restTemplate.getInterceptors().add((request, body, execution) -> {
            System.out.println("Petición a: " + request.getURI());
            System.out.println("Método: " + request.getMethod());
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}