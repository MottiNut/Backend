package com.mottinut.auth.infrastructure.persistence.entities;

import com.mottinut.auth.domain.valueobjects.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nutritionists")
@PrimaryKeyJoinColumn(name = "user_id")
public class NutritionistEntity extends UserEntity {

    // Código CNP (antes licenseNumber)
    @Column(name = "license_number", nullable = false)
    private String cnpCode;

    // Especialidad (antes specialization)
    @Column(name = "specialty", nullable = false)
    private String specialty;

    // Ubicación (antes workplace)
    @Column(name = "location", nullable = false)
    private String location;

    // Dirección
    @Column(name = "address")
    private String address;

    // Años de experiencia
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    // Biografía
    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    // Grado de maestría
    @Column(name = "master_degree")
    private String masterDegree;

    // Otra especialidad
    @Column(name = "other_specialty")
    private String otherSpecialty;

    // Aceptación de términos
    @Column(name = "accept_terms")
    private Boolean acceptTerms;

    // Imagen frontal de licencia
    @Lob
    @Column(name = "license_front_image", columnDefinition = "MEDIUMBLOB")
    private byte[] licenseFrontImage;

    // Imagen posterior de licencia
    @Lob
    @Column(name = "license_back_image", columnDefinition = "MEDIUMBLOB")
    private byte[] licenseBackImage;

    @Override
    protected void onCreate() {
        super.onCreate();
        setUserType(Role.NUTRITIONIST);
    }
}