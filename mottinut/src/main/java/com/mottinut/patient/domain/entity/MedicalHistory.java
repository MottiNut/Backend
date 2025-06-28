package com.mottinut.patient.domain.entity;

import com.mottinut.patient.domain.valueobjects.MedicalHistoryId;
import com.mottinut.patient.domain.valueobjects.PatientId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MedicalHistory {
    private final MedicalHistoryId historyId;
    private final PatientId patientId;
    private final LocalDate consultationDate;
    private final Double waistCircumference;
    private final Double hipCircumference;
    private final Double bodyFatPercentage;
    private final String bloodPressure;
    private final Integer heartRate;
    private final Double bloodGlucose;
    private final String lipidProfile;
    private final String eatingHabits;
    private final Double waterConsumption;
    private final String supplementation;
    private final Double caloricIntake;
    private final String macronutrients;
    private final String foodPreferences;
    private final String foodRelationship;
    private final Integer stressLevel;
    private final Integer sleepQuality;
    private final String nutritionalObjectives;
    private final String patientEvolution;
    private final String professionalNotes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static MedicalHistory create(PatientId patientId, LocalDate consultationDate,
                                        Double waistCircumference, Double hipCircumference,
                                        Double bodyFatPercentage, String bloodPressure,
                                        Integer heartRate, Double bloodGlucose, String lipidProfile,
                                        String eatingHabits, Double waterConsumption, String supplementation,
                                        Double caloricIntake, String macronutrients, String foodPreferences,
                                        String foodRelationship, Integer stressLevel, Integer sleepQuality,
                                        String nutritionalObjectives, String patientEvolution, String professionalNotes) {
        return new MedicalHistory(null, patientId, consultationDate, waistCircumference, hipCircumference,
                bodyFatPercentage, bloodPressure, heartRate, bloodGlucose, lipidProfile, eatingHabits,
                waterConsumption, supplementation, caloricIntake, macronutrients, foodPreferences,
                foodRelationship, stressLevel, sleepQuality, nutritionalObjectives, patientEvolution,
                professionalNotes, LocalDateTime.now(), LocalDateTime.now());
    }

    public MedicalHistory update(Double waistCircumference, Double hipCircumference,
                                 Double bodyFatPercentage, String bloodPressure, Integer heartRate,
                                 Double bloodGlucose, String lipidProfile, String eatingHabits,
                                 Double waterConsumption, String supplementation, Double caloricIntake,
                                 String macronutrients, String foodPreferences, String foodRelationship,
                                 Integer stressLevel, Integer sleepQuality, String nutritionalObjectives,
                                 String patientEvolution, String professionalNotes) {
        return new MedicalHistory(this.historyId, this.patientId, this.consultationDate,
                waistCircumference, hipCircumference, bodyFatPercentage, bloodPressure, heartRate,
                bloodGlucose, lipidProfile, eatingHabits, waterConsumption, supplementation,
                caloricIntake, macronutrients, foodPreferences, foodRelationship, stressLevel,
                sleepQuality, nutritionalObjectives, patientEvolution, professionalNotes,
                this.createdAt, LocalDateTime.now());
    }
}