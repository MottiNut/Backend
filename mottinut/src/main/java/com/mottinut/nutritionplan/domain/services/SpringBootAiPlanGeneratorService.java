package com.mottinut.nutritionplan.domain.services;

import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.nutritionplan.infrastructure.external.ai.OpenAIClient;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

public class SpringBootAiPlanGeneratorService implements AiPlanGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(SpringBootAiPlanGeneratorService.class);

    private final OpenAIClient openAIClient;
    private final UserService userService;

    public SpringBootAiPlanGeneratorService(OpenAIClient openAIClient, UserService userService) {
        this.openAIClient = openAIClient;
        this.userService = userService;
    }

    @Override
    public String generatePlan(UserId patientId, LocalDate weekStartDate, Integer energyRequirement,
                               String goal, String specialRequirements) {
        try {
            // Obtener información del paciente
            Patient patient = userService.getPatientById(patientId);

            // Construir el prompt
            String prompt = buildNutritionPlanPrompt(
                    patient, weekStartDate, energyRequirement, goal, specialRequirements
            );

            logger.info("Generando plan nutricional para paciente: {}", patientId.getValue());

            // Generar el plan usando OpenAI
            String planContent = openAIClient.generateNutritionPlan(prompt);

            logger.info("Plan nutricional generado exitosamente para paciente: {}", patientId.getValue());
            return planContent;

        } catch (NotFoundException e) {
            logger.error("Paciente no encontrado con ID: {}", patientId.getValue());
            throw new RuntimeException("Paciente no encontrado");
        } catch (Exception e) {
            logger.error("Error generando plan nutricional: ", e);
            throw new RuntimeException("Error generando plan nutricional: " + e.getMessage());
        }
    }

    private String buildNutritionPlanPrompt(Patient patient, LocalDate weekStartDate,
                                            Integer energyRequirement, String goal, String specialRequirements) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Genera un plan nutricional semanal completo:\n\n");

        // Información del paciente (más concisa)
        prompt.append("PACIENTE:\n");
        prompt.append("- Edad: ").append(calculateAge(patient.getBirthDate())).append(" años\n");
        prompt.append("- Altura: ").append(patient.getHeight()).append(" cm\n");
        prompt.append("- Peso: ").append(patient.getWeight()).append(" kg\n");

        if (patient.hasMedicalCondition()) {
            prompt.append("- Condición médica: Sí\n");
            if (patient.getChronicDisease() != null && !patient.getChronicDisease().isEmpty()) {
                prompt.append("- Enfermedad: ").append(patient.getChronicDisease()).append("\n");
            }
        }

        if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) {
            prompt.append("- Alergias: ").append(patient.getAllergies()).append("\n");
        }

        if (patient.getDietaryPreferences() != null && !patient.getDietaryPreferences().isEmpty()) {
            prompt.append("- Preferencias: ").append(patient.getDietaryPreferences()).append("\n");
        }

        // Parámetros del plan
        prompt.append("\nPLAN:\n");
        prompt.append("- Inicio: ").append(weekStartDate.toString()).append("\n");
        prompt.append("- Calorías/día: ").append(energyRequirement).append("\n");
        prompt.append("- Objetivo: ").append(goal).append("\n");

        if (specialRequirements != null && !specialRequirements.isEmpty()) {
            prompt.append("- Especiales: ").append(specialRequirements).append("\n");
        }

        // Formato de respuesta más simple pero completo
        prompt.append("\nFORMATO JSON REQUERIDO:\n");
        prompt.append("{\n");
        prompt.append("  \"weekly_summary\": {\n");
        prompt.append("    \"total_weekly_calories\": ").append(energyRequirement * 7).append(",\n");
        prompt.append("    \"average_daily_calories\": ").append(energyRequirement).append(",\n");
        prompt.append("    \"macronutrient_distribution\": {\n");
        prompt.append("      \"proteins_percentage\": 20,\n");
        prompt.append("      \"carbohydrates_percentage\": 50,\n");
        prompt.append("      \"fats_percentage\": 30\n");
        prompt.append("    },\n");
        prompt.append("    \"recommendations\": \"Recomendaciones generales\"\n");
        prompt.append("  },\n");
        prompt.append("  \"daily_plans\": [\n");

        // Ejemplo más conciso para cada día
        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (int i = 0; i < days.length; i++) {
            LocalDate currentDate = weekStartDate.plusDays(i);
            prompt.append("    {\n");
            prompt.append("      \"day\": \"").append(days[i]).append("\",\n");
            prompt.append("      \"date\": \"").append(currentDate.toString()).append("\",\n");
            prompt.append("      \"meals\": {\n");
            prompt.append("        \"breakfast\": {\"foods\": [{\"name\":\"ejemplo\",\"quantity\":\"100g\",\"calories\":200}], \"total_calories\":200},\n");
            prompt.append("        \"lunch\": {\"foods\": [{\"name\":\"ejemplo\",\"quantity\":\"150g\",\"calories\":400}], \"total_calories\":400},\n");
            prompt.append("        \"dinner\": {\"foods\": [{\"name\":\"ejemplo\",\"quantity\":\"120g\",\"calories\":350}], \"total_calories\":350},\n");
            prompt.append("        \"snacks\": {\"foods\": [{\"name\":\"ejemplo\",\"quantity\":\"50g\",\"calories\":150}], \"total_calories\":150}\n");
            prompt.append("      },\n");
            prompt.append("      \"total_calories\": ").append(energyRequirement).append(",\n");
            prompt.append("      \"macronutrients\": {\"proteins\":").append(energyRequirement * 0.2 / 4).append(",\"carbohydrates\":").append(energyRequirement * 0.5 / 4).append(",\"fats\":").append(energyRequirement * 0.3 / 9).append("}\n");
            prompt.append("    }");
            if (i < days.length - 1) {
                prompt.append(",");
            }
            prompt.append("\n");
        }

        prompt.append("  ]\n");
        prompt.append("}\n\n");

        prompt.append("INSTRUCCIONES CRÍTICAS:\n");
        prompt.append("- OBLIGATORIO: Incluir EXACTAMENTE 7 días completos\n");
        prompt.append("- Cada día DEBE tener las 4 comidas\n");
        prompt.append("- Sumar ~").append(energyRequirement).append(" kcal/día\n");
        prompt.append("- Respetar alergias y preferencias\n");
        prompt.append("- JSON válido sin texto extra\n");
        prompt.append("- Si te quedas sin espacio, usar menos alimentos por comida pero incluir todos los días\n");

        return prompt.toString();
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}