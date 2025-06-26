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
                               String goal, String specialRequirements, Integer mealsPerDay) {
        // Validar número de comidas (valor por defecto si es null)
        if (mealsPerDay == null) {
            mealsPerDay = 4; // Valor por defecto
        }
        if (mealsPerDay < 3 || mealsPerDay > 6) {
            throw new IllegalArgumentException("El número de comidas por día debe estar entre 3 y 6");
        }
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
            // Obtener el historial médico más reciente
            MedicalHistory latestHistory = medicalHistories.stream()
                    .max(java.util.Comparator.comparing(MedicalHistory::getConsultationDate))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener el historial médico más reciente"));

            String prompt = buildNutritionPlanPrompt(patient, latestHistory, weekStartDate,
                    energyRequirement, goal, specialRequirements, mealsPerDay);

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

    private String buildNutritionPlanPrompt(Patient patient, MedicalHistory medicalHistory,
                                            LocalDate weekStartDate, Integer energyRequirement,
                                            String goal, String specialRequirements, Integer mealsPerDay) {
        StringBuilder prompt = new StringBuilder();

        // CONTEXTO Y OBJETIVO PRINCIPAL
        prompt.append("Eres un nutricionista experto. Genera un plan nutricional semanal de 7 días completos ")
                .append("con comidas típicas peruanas que sean fáciles de preparar.\n\n");

        // PERFIL DEL PACIENTE (CONDENSADO)
        prompt.append("PACIENTE:\n");
        appendPatientBasicInfo(prompt, patient);
        appendMedicalConditions(prompt, patient);
        appendKeyMedicalHistory(prompt, medicalHistory);

        // PARÁMETROS DEL PLAN
        prompt.append("\nPLAN REQUERIDO:\n");
        prompt.append("- Inicio: ").append(weekStartDate).append("\n");
        prompt.append("- Calorías/día: ").append(energyRequirement).append("\n");
        prompt.append("- Comidas/día: ").append(mealsPerDay).append("\n");
        prompt.append("- Objetivo: ").append(goal).append("\n");

        if (specialRequirements != null && !specialRequirements.isEmpty()) {
            prompt.append("- Requisitos especiales: ").append(specialRequirements).append("\n");
        }

        // FORMATO FLEXIBLE
        prompt.append("\nFORMATO DE RESPUESTA (JSON):\n");
        prompt.append("{\n");
        prompt.append("  \"plan_summary\": {\n");
        prompt.append("    \"weekly_calories\": ").append(energyRequirement * 7).append(",\n");
        prompt.append("    \"daily_calories\": ").append(energyRequirement).append(",\n");
        prompt.append("    \"meals_per_day\": ").append(mealsPerDay).append(",\n");
        prompt.append("    \"macros\": {\"protein_%\": 20, \"carbs_%\": 50, \"fat_%\": 30}\n");
        prompt.append("  },\n");
        prompt.append("  \"days\": [\n");

        // Ejemplo de UN día para mostrar el formato
        LocalDate exampleDate = weekStartDate;
        prompt.append("    {\n");
        prompt.append("      \"day\": \"Lunes\",\n");
        prompt.append("      \"date\": \"").append(exampleDate).append("\",\n");
        prompt.append("      \"meals\": [\n");

        // Generar comidas ejemplo según el número solicitado
        int caloriesPerMeal = energyRequirement / mealsPerDay;
        String[] mealTypes = getMealTypes(mealsPerDay);

        for (int i = 0; i < mealsPerDay; i++) {
            prompt.append("        {\n");
            prompt.append("          \"type\": \"").append(mealTypes[i]).append("\",\n");
            prompt.append("          \"name\": \"Nombre del plato peruano\",\n");
            prompt.append("          \"description\": \"Breve descripción del plato y sus beneficios nutricionales\",\n");
            prompt.append("          \"ingredients\": \"Lista de ingredientes con cantidades específicas\",\n");
            prompt.append("          \"calories\": ").append(caloriesPerMeal).append(",\n");
            prompt.append("          \"preparation_time\": \"15 min\"\n");
            prompt.append("        }");
            if (i < mealsPerDay - 1) prompt.append(",");
            prompt.append("\n");
        }

        prompt.append("    {\n");
        prompt.append("      \"day\": \"Martes\",\n");
        prompt.append("      \"date\": \"").append(exampleDate.plusDays(1)).append("\",\n");
        prompt.append("      \"meals\": [...],\n");
        prompt.append("      \"total_calories\": ").append(energyRequirement).append("\n");
        prompt.append("    },\n");
        prompt.append("    // ... continuar hasta completar los 7 días (Lunes a Domingo)\n");

        prompt.append("  ]\n");
        prompt.append("}\n\n");

        // INSTRUCCIONES FINALES
        prompt.append("IMPORTANTE:\n");
        prompt.append("- Generar EXACTAMENTE 7 días completos: Lunes, Martes, Miércoles, Jueves, Viernes, Sábado, Domingo\n");
        prompt.append("- Fechas consecutivas comenzando desde ").append(weekStartDate).append("\n");
        prompt.append("- Cada día debe tener ").append(mealsPerDay).append(" comidas\n");
        prompt.append("- JSON válido y completo, sin comentarios internos\n");
        prompt.append("- Solo JSON, sin texto adicional antes o después\n");

        return prompt.toString();
    }

    private void appendPatientBasicInfo(StringBuilder prompt, Patient patient) {
        prompt.append("- Edad: ").append(calculateAge(patient.getBirthDate())).append(" años\n");
        prompt.append("- Género: ").append(patient.getGender() != null ? patient.getGender() : "No especificado").append("\n");
        prompt.append("- IMC: ").append(String.format("%.1f", patient.calculateBMI())).append("\n");
    }

    private void appendMedicalConditions(StringBuilder prompt, Patient patient) {
        if (patient.hasMedicalCondition() ||
                (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) ||
                (patient.getDietaryPreferences() != null && !patient.getDietaryPreferences().isEmpty())) {

            prompt.append("- Condiciones: ");

            if (patient.getChronicDisease() != null && !patient.getChronicDisease().isEmpty()) {
                prompt.append(patient.getChronicDisease()).append("; ");
            }
            if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) {
                prompt.append("Alergias: ").append(patient.getAllergies()).append("; ");
            }
            if (patient.getDietaryPreferences() != null && !patient.getDietaryPreferences().isEmpty()) {
                prompt.append("Preferencias: ").append(patient.getDietaryPreferences());
            }
            prompt.append("\n");
        }
    }

    private void appendKeyMedicalHistory(StringBuilder prompt, MedicalHistory history) {
        prompt.append("\nHISTORIAL CLAVE:\n");

        // Solo los datos más relevantes para reducir tamaño
        if (history.getBloodGlucose() != null) {
            prompt.append("- Glucosa: ").append(history.getBloodGlucose()).append(" mg/dL\n");
        }
        if (history.getBloodPressure() != null && !history.getBloodPressure().isEmpty()) {
            prompt.append("- Presión: ").append(history.getBloodPressure()).append("\n");
        }
        if (history.getBodyFatPercentage() != null) {
            prompt.append("- Grasa corporal: ").append(history.getBodyFatPercentage()).append("%\n");
        }
        if (history.getEatingHabits() != null && !history.getEatingHabits().isEmpty()) {
            prompt.append("- Hábitos actuales: ").append(limitText(history.getEatingHabits(), 100)).append("\n");
        }
        if (history.getNutritionalObjectives() != null && !history.getNutritionalObjectives().isEmpty()) {
            prompt.append("- Objetivos: ").append(limitText(history.getNutritionalObjectives(), 100)).append("\n");
        }
    }

    private String[] getMealTypes(int mealsPerDay) {
        String[] allMealTypes = {"Desayuno", "Media mañana", "Almuerzo", "Merienda", "Cena", "Cena tardía"};

        if (mealsPerDay <= 3) {
            return new String[]{"Desayuno", "Almuerzo", "Cena"};
        } else if (mealsPerDay == 4) {
            return new String[]{"Desayuno", "Almuerzo", "Merienda", "Cena"};
        } else if (mealsPerDay == 5) {
            return new String[]{"Desayuno", "Media mañana", "Almuerzo", "Merienda", "Cena"};
        } else {
            return java.util.Arrays.copyOf(allMealTypes, Math.min(mealsPerDay, allMealTypes.length));
        }
    }

    private String limitText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}