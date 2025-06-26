package com.mottinut.nutritionplan.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_plans")
@Getter
@Setter
public class NutritionPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "nutritionist_id", nullable = false)
    private Long nutritionistId;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "energy_requirement")
    private Integer energyRequirement;

    @Column(name = "goal")
    private String goal;

    @Column(name = "special_requirements", columnDefinition = "TEXT")
    private String specialRequirements;

    @Column(name = "meals_per_day")
    private Integer mealsPerDay;

    @Column(name = "plan_content", columnDefinition = "TEXT")
    private String planContent;

    @Column(name = "status", nullable = false)
    private String status = "pending_review";

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "patient_feedback", columnDefinition = "TEXT")
    private String patientFeedback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "patient_response_at")
    private LocalDateTime patientResponseAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

