package com.mottinut.nutritionplan.infrastructure.external.ai;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.nutritionplan.domain.services.AiPlanGeneratorService;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Service
public class FastApiPlanGeneratorService implements AiPlanGeneratorService {
    private final RestTemplate restTemplate;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(FastApiPlanGeneratorService.class);

    @Value("${ai.fastapi.url:http://localhost:8001}")
    private String fastApiUrl;

    public FastApiPlanGeneratorService(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    @Override
    public String generatePlan(UserId patientId, LocalDate weekStartDate, Integer energyRequirement,
                               String goal, String specialRequirements) {
        try {
            // Get patient info
            System.out.println("ESTOY AQUI MIRAMEEEEEEEEEEEEEE 1:");
            User patient = userService.getUserById(patientId);
            System.out.println("ESTOY AQUI MIRAMEEEEEEEEEEEEEE 2:");

            // Prepare patient info map - usando HashMap para permitir nulls
            Map<String, Object> patientInfo = new HashMap<>();
            patientInfo.put("age", calculateAge(patient.getBirthDate()));
            patientInfo.put("height", patient.getHeight());
            patientInfo.put("weight", patient.getWeight());
            patientInfo.put("has_medical_condition", patient.hasMedicalCondition());
            patientInfo.put("chronic_disease", patient.getChronicDisease() != null ? patient.getChronicDisease() : "");
            patientInfo.put("allergies", patient.getAllergies() != null ? patient.getAllergies() : "");
            patientInfo.put("dietary_preferences", patient.getDietaryPreferences() != null ? patient.getDietaryPreferences() : "");

            // Prepare request for FastAPI
            Map<String, Object> request = new HashMap<>();
            request.put("patient_info", patientInfo);
            request.put("week_start_date", weekStartDate.toString());
            request.put("energy_requirement", energyRequirement);
            request.put("goal", goal);
            request.put("special_requirements", specialRequirements != null ? specialRequirements : "");

            System.out.println("Request a FastAPI:");
            System.out.println(request);

            // Call FastAPI
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    fastApiUrl + "/generate-nutrition-plan", entity, String.class);

            System.out.println("Respuesta desde FastAPI:");
            System.out.println(response.getBody());
            return response.getBody();

        } catch (ResourceAccessException e) {
            logger.error("No se pudo conectar a FastAPI en: {}", fastApiUrl);
            throw new RuntimeException("Servicio de IA no disponible. Verifique que FastAPI esté corriendo.");
        } catch (HttpClientErrorException e) {
            logger.error("Error HTTP {} de FastAPI: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error en petición a IA: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error("Error del servidor FastAPI: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error interno del servicio de IA");
        } catch (Exception e) {
            logger.error("Error inesperado: ", e);
            throw new RuntimeException("Error generando plan nutricional: " + e.getMessage());
        }
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
