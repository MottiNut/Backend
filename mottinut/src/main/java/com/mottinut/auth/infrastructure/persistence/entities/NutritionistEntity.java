package com.mottinut.auth.infrastructure.persistence.entities;

import com.mottinut.auth.domain.valueobjects.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nutritionists")
@PrimaryKeyJoinColumn(name = "user_id") // Clave foránea hacia users
public class NutritionistEntity extends UserEntity {

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private String workplace;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    private String biography;

    @Override
    protected void onCreate() {
        super.onCreate();
        setUserType(Role.NUTRITIONIST);
    }
}
