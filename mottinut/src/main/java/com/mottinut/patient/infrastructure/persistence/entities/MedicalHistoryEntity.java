package com.mottinut.patient.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "consultation_date", nullable = false)
    private LocalDate consultationDate;

    @Column(name = "waist_circumference")
    private Double waistCircumference;

    @Column(name = "hip_circumference")
    private Double hipCircumference;

    @Column(name = "body_fat_percentage")
    private Double bodyFatPercentage;

    @Column(name = "blood_pressure")
    private String bloodPressure;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "blood_glucose")
    private Double bloodGlucose;

    @Column(name = "lipid_profile")
    private String lipidProfile;

    @Column(name = "eating_habits")
    private String eatingHabits;

    @Column(name = "water_consumption")
    private Double waterConsumption;

    private String supplementation;

    @Column(name = "caloric_intake")
    private Double caloricIntake;

    private String macronutrients;

    @Column(name = "food_preferences")
    private String foodPreferences;

    @Column(name = "food_relationship")
    private String foodRelationship;

    @Column(name = "stress_level")
    private Integer stressLevel;

    @Column(name = "sleep_quality")
    private Integer sleepQuality;

    @Column(name = "nutritional_objectives")
    private String nutritionalObjectives;

    @Column(name = "patient_evolution")
    private String patientEvolution;

    @Column(name = "professional_notes")
    private String professionalNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
