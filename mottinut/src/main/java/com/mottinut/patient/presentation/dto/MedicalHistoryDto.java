package com.mottinut.patient.presentation.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryDto {
    private Long historyId;
    private Long patientId;
    private LocalDate consultationDate;
    private Double waistCircumference;
    private Double hipCircumference;
    private Double bodyFatPercentage;
    private String bloodPressure;
    private Integer heartRate;
    private Double bloodGlucose;
    private String lipidProfile;
    private String eatingHabits;
    private Double waterConsumption;
    private String supplementation;
    private Double caloricIntake;
    private String macronutrients;
    private String foodPreferences;
    private String foodRelationship;
    private Integer stressLevel;
    private Integer sleepQuality;
    private String nutritionalObjectives;
    private String patientEvolution;
    private String professionalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
