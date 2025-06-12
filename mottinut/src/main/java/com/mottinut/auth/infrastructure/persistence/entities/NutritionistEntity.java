package com.mottinut.auth.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("NUTRITIONIST")
public class NutritionistEntity extends UserEntity {
    @Column(name = "license_number")
    private String licenseNumber;

    private String specialization;

    private String workplace;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    private String biography;

}
