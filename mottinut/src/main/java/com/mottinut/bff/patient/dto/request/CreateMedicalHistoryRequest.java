package com.mottinut.bff.patient.dto.request;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalHistoryRequest {
    @NotNull(message = "La fecha de consulta es requerida")
    private LocalDate consultationDate;
    // Nuevos getters y setters
    @Getter
    @Setter
    private Double height;
    @Getter
    @Setter
    private Double weight;
    private Double waistCircumference;
    private Double hipCircumference;
    private Double bodyFatPercentage;
    private String bloodPressure;

    @Min(value = 0, message = "La frecuencia cardíaca debe ser positiva")
    private Integer heartRate;

    @Min(value = 0, message = "La glucosa debe ser positiva")
    private Double bloodGlucose;

    private String lipidProfile;
    private String eatingHabits;

    @Min(value = 0, message = "El consumo de agua debe ser positivo")
    private Double waterConsumption;

    private String supplementation;

    @Min(value = 0, message = "La ingesta calórica debe ser positiva")
    private Double caloricIntake;

    private String macronutrients;
    private String foodPreferences;
    private String foodRelationship;

    @Min(value = 1, message = "El nivel de estrés debe estar entre 1 y 10")
    @Max(value = 10, message = "El nivel de estrés debe estar entre 1 y 10")
    private Integer stressLevel;

    @Min(value = 1, message = "La calidad del sueño debe estar entre 1 y 10")
    @Max(value = 10, message = "La calidad del sueño debe estar entre 1 y 10")
    private Integer sleepQuality;

    private String nutritionalObjectives;
    private String patientEvolution;
    private String professionalNotes;

}
