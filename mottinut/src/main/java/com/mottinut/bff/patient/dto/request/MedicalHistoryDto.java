package com.mottinut.bff.patient.dto.request;

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
    private Double bloodGlucose;
    private Double waterConsumption;
    private Double caloricIntake;
    private String bloodPressure;
    private String lipidProfile;
    private String eatingHabits;
    private String supplementation;
    private String macronutrients;
    private String foodPreferences;
    private String foodRelationship;
    private String nutritionalObjectives;
    private String patientEvolution;
    private String professionalNotes;
    private Integer heartRate;
    private Integer stressLevel;
    private Integer sleepQuality;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // New calculated fields for BFF
    private Double waistHipRatio;
}
