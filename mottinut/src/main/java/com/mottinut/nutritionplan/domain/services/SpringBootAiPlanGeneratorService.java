package com.mottinut.nutritionplan.domain.services;

import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.nutritionplan.infrastructure.external.ai.OpenAIClient;
import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.repositories.MedicalHistoryRepository;
import com.mottinut.patient.domain.valueobjects.PatientId;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class SpringBootAiPlanGeneratorService implements AiPlanGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootAiPlanGeneratorService.class);

    private final OpenAIClient openAIClient;
    private final UserService userService;
    private final MedicalHistoryRepository medicalHistoryRepository;

    public SpringBootAiPlanGeneratorService(OpenAIClient openAIClient,
                                            UserService userService,
                                            MedicalHistoryRepository medicalHistoryRepository) {
        this.openAIClient = openAIClient;
        this.userService = userService;
        this.medicalHistoryRepository = medicalHistoryRepository;
    }

    @Override
    public String generatePlan(UserId patientId, LocalDate weekStartDate, Integer energyRequirement,
                               String goal, String specialRequirements) {
        try {
            // Obtener el paciente
            Patient patient = userService.getPatientById(patientId);

            // Verificar que tenga historial médico
            List<MedicalHistory> medicalHistories = medicalHistoryRepository.findByPatientId(new PatientId(patientId.getValue()));
            if (medicalHistories.isEmpty()) {
                logger.error("No se puede generar plan nutricional - Paciente sin historial médico: {}", patientId.getValue());
                throw new IllegalStateException("No se puede generar el plan nutricional. El paciente debe tener al menos un historial médico registrado por un nutricionista.");
            }

            // Obtener el historial médico más reciente
            MedicalHistory latestHistory = medicalHistories.stream()
                    .max(java.util.Comparator.comparing(MedicalHistory::getConsultationDate))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener el historial médico más reciente"));

            String prompt = buildNutritionPlanPromptWithMedicalHistory(patient, latestHistory, weekStartDate,
                    energyRequirement, goal, specialRequirements);

            logger.info("Generando plan nutricional para paciente: {} con historial médico del: {}",
                    patientId.getValue(), latestHistory.getConsultationDate());

            String planContent = openAIClient.generateNutritionPlan(prompt);

            logger.info("Plan nutricional generado exitosamente para paciente: {}", patientId.getValue());
            return planContent;

        } catch (NotFoundException e) {
            logger.error("Paciente no encontrado con ID: {}", patientId.getValue());
            throw new RuntimeException("Paciente no encontrado");
        } catch (IllegalStateException e) {
            logger.error("Error de validación: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error generando plan nutricional: ", e);
            throw new RuntimeException("Error generando plan nutricional: " + e.getMessage());
        }
    }

    private String buildNutritionPlanPromptWithMedicalHistory(Patient patient, MedicalHistory medicalHistory,
                                                              LocalDate weekStartDate, Integer energyRequirement,
                                                              String goal, String specialRequirements) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Genera un plan nutricional semanal completo basado en el perfil médico detallado:\n\n");

        // INFORMACIÓN BÁSICA DEL PACIENTE
        prompt.append("PACIENTE:\n");
        prompt.append("- Edad: ").append(calculateAge(patient.getBirthDate())).append(" años\n");
        prompt.append("- Género: ").append(patient.getGender() != null ? patient.getGender() : "No especificado").append("\n");
        prompt.append("- Altura: ").append(patient.getHeight()).append(" cm\n");
        prompt.append("- Peso: ").append(patient.getWeight()).append(" kg\n");
        prompt.append("- IMC: ").append(String.format("%.2f", patient.calculateBMI())).append("\n");

        // CONDICIONES MÉDICAS BÁSICAS
        if (patient.hasMedicalCondition()) {
            prompt.append("- Condición médica: Sí\n");
            if (patient.getChronicDisease() != null && !patient.getChronicDisease().isEmpty()) {
                prompt.append("- Enfermedad crónica: ").append(patient.getChronicDisease()).append("\n");
            }
        }

        if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) {
            prompt.append("- Alergias: ").append(patient.getAllergies()).append("\n");
        }

        if (patient.getDietaryPreferences() != null && !patient.getDietaryPreferences().isEmpty()) {
            prompt.append("- Preferencias dietéticas: ").append(patient.getDietaryPreferences()).append("\n");
        }

        // HISTORIAL MÉDICO DETALLADO
        prompt.append("\nHISTORIAL MÉDICO (Consulta: ").append(medicalHistory.getConsultationDate()).append("):\n");

        // Medidas antropométricas
        if (medicalHistory.getWaistCircumference() != null) {
            prompt.append("- Circunferencia de cintura: ").append(medicalHistory.getWaistCircumference()).append(" cm\n");
        }
        if (medicalHistory.getHipCircumference() != null) {
            prompt.append("- Circunferencia de cadera: ").append(medicalHistory.getHipCircumference()).append(" cm\n");
        }
        if (medicalHistory.getBodyFatPercentage() != null) {
            prompt.append("- Porcentaje de grasa corporal: ").append(medicalHistory.getBodyFatPercentage()).append("%\n");
        }

        // Parámetros clínicos
        if (medicalHistory.getBloodPressure() != null && !medicalHistory.getBloodPressure().isEmpty()) {
            prompt.append("- Presión arterial: ").append(medicalHistory.getBloodPressure()).append("\n");
        }
        if (medicalHistory.getHeartRate() != null) {
            prompt.append("- Frecuencia cardíaca: ").append(medicalHistory.getHeartRate()).append(" lpm\n");
        }
        if (medicalHistory.getBloodGlucose() != null) {
            prompt.append("- Glucosa en sangre: ").append(medicalHistory.getBloodGlucose()).append(" mg/dL\n");
        }
        if (medicalHistory.getLipidProfile() != null && !medicalHistory.getLipidProfile().isEmpty()) {
            prompt.append("- Perfil lipídico: ").append(medicalHistory.getLipidProfile()).append("\n");
        }

        // Hábitos alimentarios y nutricionales
        if (medicalHistory.getEatingHabits() != null && !medicalHistory.getEatingHabits().isEmpty()) {
            prompt.append("- Hábitos alimentarios: ").append(medicalHistory.getEatingHabits()).append("\n");
        }
        if (medicalHistory.getWaterConsumption() != null) {
            prompt.append("- Consumo de agua: ").append(medicalHistory.getWaterConsumption()).append(" L/día\n");
        }
        if (medicalHistory.getSupplementation() != null && !medicalHistory.getSupplementation().isEmpty()) {
            prompt.append("- Suplementación: ").append(medicalHistory.getSupplementation()).append("\n");
        }
        if (medicalHistory.getCaloricIntake() != null) {
            prompt.append("- Ingesta calórica actual: ").append(medicalHistory.getCaloricIntake()).append(" kcal/día\n");
        }
        if (medicalHistory.getMacronutrients() != null && !medicalHistory.getMacronutrients().isEmpty()) {
            prompt.append("- Distribución de macronutrientes actual: ").append(medicalHistory.getMacronutrients()).append("\n");
        }
        if (medicalHistory.getFoodPreferences() != null && !medicalHistory.getFoodPreferences().isEmpty()) {
            prompt.append("- Preferencias alimentarias: ").append(medicalHistory.getFoodPreferences()).append("\n");
        }
        if (medicalHistory.getFoodRelationship() != null && !medicalHistory.getFoodRelationship().isEmpty()) {
            prompt.append("- Relación con la comida: ").append(medicalHistory.getFoodRelationship()).append("\n");
        }

        // Estilo de vida
        if (medicalHistory.getStressLevel() != null) {
            prompt.append("- Nivel de estrés (1-10): ").append(medicalHistory.getStressLevel()).append("\n");
        }
        if (medicalHistory.getSleepQuality() != null) {
            prompt.append("- Calidad del sueño (1-10): ").append(medicalHistory.getSleepQuality()).append("\n");
        }

        // Objetivos y evolución
        if (medicalHistory.getNutritionalObjectives() != null && !medicalHistory.getNutritionalObjectives().isEmpty()) {
            prompt.append("- Objetivos nutricionales: ").append(medicalHistory.getNutritionalObjectives()).append("\n");
        }
        if (medicalHistory.getPatientEvolution() != null && !medicalHistory.getPatientEvolution().isEmpty()) {
            prompt.append("- Evolución del paciente: ").append(medicalHistory.getPatientEvolution()).append("\n");
        }
        if (medicalHistory.getProfessionalNotes() != null && !medicalHistory.getProfessionalNotes().isEmpty()) {
            prompt.append("- Notas profesionales: ").append(medicalHistory.getProfessionalNotes()).append("\n");
        }

        // PLAN SOLICITADO
        prompt.append("\nPLAN SOLICITADO:\n");
        prompt.append("- Inicio: ").append(weekStartDate.toString()).append("\n");
        prompt.append("- Calorías objetivo/día: ").append(energyRequirement).append("\n");
        prompt.append("- Objetivo: ").append(goal).append("\n");

        if (specialRequirements != null && !specialRequirements.isEmpty()) {
            prompt.append("- Requerimientos especiales: ").append(specialRequirements).append("\n");
        }

        // FORMATO JSON (resto del código igual)
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
        prompt.append("    \"recommendations\": \"Recomendaciones basadas en el historial médico\"\n");
        prompt.append("  },\n");
        prompt.append("  \"daily_plans\": [\n");

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
            prompt.append("      \"macronutrients\": {\"proteins\":").append(energyRequirement * 0.2 / 4)
                    .append(",\"carbohydrates\":").append(energyRequirement * 0.5 / 4)
                    .append(",\"fats\":").append(energyRequirement * 0.3 / 9).append("}\n");
            prompt.append("    }");
            if (i < days.length - 1) {
                prompt.append(",");
            }
            prompt.append("\n");
        }
        prompt.append("  ]\n");
        prompt.append("}\n\n");

        prompt.append("INSTRUCCIONES CRÍTICAS:\n");
        prompt.append("- OBLIGATORIO: Considerar TODA la información del historial médico\n");
        prompt.append("- Adaptar el plan según las condiciones médicas y parámetros clínicos\n");
        prompt.append("- Respetar restricciones alimentarias y alergias\n");
        prompt.append("- Considerar el nivel de estrés y calidad del sueño\n");
        prompt.append("- Es un contexto peruano, elige comidas que se puedan elaborar en Perú\n");
        prompt.append("- Incluir EXACTAMENTE 7 días completos\n");
        prompt.append("- Cada día DEBE tener las 4 comidas\n");
        prompt.append("- Sumar ~").append(energyRequirement).append(" kcal/día\n");
        prompt.append("- JSON válido sin texto extra\n");
        prompt.append("- Si te quedas sin espacio, usar menos alimentos por comida pero incluir todos los días\n");

        return prompt.toString();
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}