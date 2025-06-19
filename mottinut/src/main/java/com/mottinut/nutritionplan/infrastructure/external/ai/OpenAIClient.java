package com.mottinut.nutritionplan.infrastructure.external.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenAIClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIClient.class);

    @Value("${ai.openrouter.api-key}")
    private String apiKey;

    @Value("${ai.openrouter.base-url:https://openrouter.ai/api/v1}")
    private String baseUrl;

    @Value("${ai.openrouter.model:deepseek/deepseek-r1:free}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAIClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String generateNutritionPlan(String prompt) throws Exception {
        return generateNutritionPlanWithRetry(prompt, 3);
    }

    private String generateNutritionPlanWithRetry(String prompt, int maxRetries) throws Exception {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("Intento {} de {} para generar plan nutricional", attempt, maxRetries);

                // Construir el request body con parámetros optimizados
                Map<String, Object> requestBody = buildRequestBody(prompt, attempt);

                // Configurar headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                // Hacer la petición
                ResponseEntity<String> response = restTemplate.postForEntity(
                        baseUrl + "/chat/completions", entity, String.class);

                // Parsear la respuesta
                JsonNode responseJson = objectMapper.readTree(response.getBody());

                // Verificar si la respuesta fue cortada
                String finishReason = responseJson.get("choices").get(0).get("finish_reason").asText();
                String content = responseJson.get("choices").get(0).get("message").get("content").asText();

                logger.info("Respuesta recibida. Finish reason: {}, Longitud: {}", finishReason, content.length());

                // Si fue cortada por límite de tokens, intentar con menos días o tokens
                if ("length".equals(finishReason)) {
                    logger.warn("Respuesta cortada por límite de tokens en intento {}", attempt);
                    if (attempt < maxRetries) {
                        continue; // Reintentar con parámetros ajustados
                    }
                }

                // Limpiar y validar el JSON
                String cleanedContent = cleanJsonResponse(content);

                // Validar que sea JSON válido
                try {
                    JsonNode validatedJson = objectMapper.readTree(cleanedContent);

                    // Verificar que tenga la estructura básica esperada
                    if (validateNutritionPlanStructure(validatedJson)) {
                        logger.info("Plan nutricional generado exitosamente en intento {}", attempt);
                        return cleanedContent;
                    } else {
                        throw new RuntimeException("JSON generado no tiene la estructura esperada");
                    }

                } catch (Exception e) {
                    logger.error("El contenido generado no es JSON válido en intento {}: {}", attempt, cleanedContent);
                    lastException = new RuntimeException("La IA no generó un JSON válido: " + e.getMessage());
                    if (attempt < maxRetries) {
                        continue;
                    }
                }

            } catch (ResourceAccessException e) {
                logger.error("Error de conectividad con OpenRouter en intento {}: {}", attempt, e.getMessage());
                lastException = new RuntimeException("Servicio de IA no disponible. Verifique la conexión a internet.");
                if (attempt < maxRetries) {
                    // Esperar antes de reintentar
                    try {
                        Thread.sleep(2000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
            } catch (HttpClientErrorException e) {
                logger.error("Error HTTP {} de OpenRouter en intento {}: {}", e.getStatusCode(), attempt, e.getResponseBodyAsString());
                lastException = new RuntimeException("Error en petición a IA: " + e.getMessage());
                break; // No reintentar en errores 4xx
            } catch (HttpServerErrorException e) {
                logger.error("Error del servidor OpenRouter en intento {}: {}", attempt, e.getResponseBodyAsString());
                lastException = new RuntimeException("Error interno del servicio de IA");
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(3000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
            } catch (Exception e) {
                logger.error("Error inesperado al generar plan en intento {}: ", attempt, e);
                lastException = new RuntimeException("Error generando plan nutricional: " + e.getMessage());
                if (attempt < maxRetries) {
                    continue;
                }
            }
        }

        throw lastException != null ? lastException : new RuntimeException("Error desconocido generando plan");
    }

    private Map<String, Object> buildRequestBody(String prompt, int attempt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        // Ajustar parámetros según el intento
        if (attempt == 1) {
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 6000); // Aumentar tokens máximos
        } else if (attempt == 2) {
            requestBody.put("temperature", 0.2);
            requestBody.put("max_tokens", 8000); // Aumentar más en segundo intento
        } else {
            requestBody.put("temperature", 0.1);
            requestBody.put("max_tokens", 4000); // Reducir en último intento para asegurar completitud
        }

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", buildSystemPrompt(attempt));
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);
        return requestBody;
    }

    private String buildSystemPrompt(int attempt) {
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("Eres un nutricionista profesional. ");

        if (attempt <= 2) {
            systemPrompt.append("Responde ÚNICAMENTE con JSON válido, sin texto adicional antes o después. ");
            systemPrompt.append("Es CRÍTICO que completes toda la estructura JSON incluyendo los 7 días completos. ");
            systemPrompt.append("Si te quedas sin espacio, prioriza incluir todos los días aunque con menos detalle en los alimentos.");
        } else {
            systemPrompt.append("Responde ÚNICAMENTE con JSON válido. ");
            systemPrompt.append("IMPORTANTE: Genera un plan MÁS CONCISO pero COMPLETO con los 7 días. ");
            systemPrompt.append("Usa menos alimentos por comida pero asegúrate de incluir los 7 días completos.");
        }

        return systemPrompt.toString();
    }

    private boolean validateNutritionPlanStructure(JsonNode json) {
        try {
            // Verificar estructura básica - CORREGIR los nombres de campos
            if (!json.has("plan_summary") || !json.has("days")) {
                logger.error("JSON no tiene la estructura básica requerida. Campos encontrados: {}",
                        json.fieldNames().toString());
                return false;
            }

            JsonNode days = json.get("days");
            if (!days.isArray()) {
                logger.error("'days' no es un array");
                return false;
            }

            int daysCount = days.size();
            logger.info("Plan generado con {} días", daysCount);

            // Verificar que tenga exactamente 7 días (o al menos 5 si se acepta incompleto)
            if (daysCount < 7) {
                logger.warn("Plan tiene menos de 7 días: {}. Verificando si tiene al menos 5...", daysCount);
                if (daysCount < 5) {
                    logger.error("Plan tiene menos de 5 días: {}", daysCount);
                    return false;
                }
            }

            // Verificar que cada día tenga la estructura básica
            for (JsonNode day : days) {
                if (!day.has("day") || !day.has("meals")) {
                    logger.error("Día sin estructura básica: {}", day);
                    return false;
                }

                JsonNode meals = day.get("meals");
                if (!meals.isArray() || meals.size() == 0) {
                    logger.error("Día sin comidas válidas");
                    return false;
                }
            }

            // Verificar plan_summary
            JsonNode planSummary = json.get("plan_summary");
            if (!planSummary.has("daily_calories") || !planSummary.has("meals_per_day")) {
                logger.error("plan_summary no tiene la estructura esperada");
                return false;
            }

            logger.info("Validación exitosa: Plan con {} días validado correctamente", daysCount);
            return true;

        } catch (Exception e) {
            logger.error("Error validando estructura del plan: ", e);
            return false;
        }
    }

    /**
     * Limpia la respuesta de la IA para asegurar que sea JSON válido
     */
    private String cleanJsonResponse(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Respuesta vacía de la IA");
        }

        logger.debug("Contenido original: {}", content.substring(0, Math.min(200, content.length())));

        // Remover posibles marcadores de código
        content = content.replaceAll("```json", "");
        content = content.replaceAll("```", "");

        // Trimear espacios en blanco
        content = content.trim();

        // Si la respuesta no empieza con {, buscar el primer {
        int jsonStart = content.indexOf('{');
        if (jsonStart > 0) {
            content = content.substring(jsonStart);
        }

        // Intentar reparar JSON incompleto
        content = repairIncompleteJson(content);

        logger.debug("Contenido limpiado: {}", content.substring(0, Math.min(200, content.length())));

        return content;
    }

    /**
     * Intenta reparar JSON que puede estar incompleto
     */
    private String repairIncompleteJson(String json) {
        try {
            // Intentar parsear primero para ver si ya está completo
            objectMapper.readTree(json);
            return json; // Ya está completo
        } catch (Exception e) {
            logger.info("JSON incompleto, intentando reparar...");
        }

        // Contar llaves abiertas vs cerradas
        int openBraces = 0;
        int closeBraces = 0;
        for (char c : json.toCharArray()) {
            if (c == '{') openBraces++;
            if (c == '}') closeBraces++;
        }

        // Agregar llaves faltantes
        StringBuilder repairedJson = new StringBuilder(json);
        int missingBraces = openBraces - closeBraces;

        if (missingBraces > 0) {
            logger.info("Agregando {} llaves faltantes", missingBraces);

            // Si termina con coma, removerla
            if (repairedJson.toString().trim().endsWith(",")) {
                int lastComma = repairedJson.lastIndexOf(",");
                repairedJson.deleteCharAt(lastComma);
            }

            // Agregar llaves de cierre
            for (int i = 0; i < missingBraces; i++) {
                repairedJson.append("}");
            }
        }

        return repairedJson.toString();
    }
}