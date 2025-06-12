package com.mottinut.auth.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("PATIENT")
public class PatientEntity extends UserEntity {
    private Double height;
    private Double weight;

    @Column(name = "has_medical_condition")
    private Boolean hasMedicalCondition = false;

    @Column(name = "chronic_disease")
    private String chronicDisease;

    private String allergies;

    @Column(name = "dietary_preferences")
    private String dietaryPreferences;

    @Column(name = "emergency_contact")
    private String emergencyContact;

}
