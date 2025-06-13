package com.mottinut.auth.infrastructure.persistence.entities;

import com.mottinut.auth.domain.valueobjects.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "patients")
@PrimaryKeyJoinColumn(name = "user_id") // Clave foránea hacia users
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

    @Override
    protected void onCreate() {
        super.onCreate();
        setUserType(Role.PATIENT);
    }
}
