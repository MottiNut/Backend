package com.mottinut.auth.presentation.dto;

import com.mottinut.auth.domain.entities.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class PatientResponse {
    // Campos comunes de User
    private Long userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate birthDate;
    private String phone;
    private LocalDateTime createdAt;

    // Campos específicos de Patient
    private Double height;
    private Double weight;
    private Double bmi; // Calculado
    private String bmiCategory; // Calculado
    private Boolean hasMedicalCondition;
    private String chronicDisease;
    private String allergies;
    private String dietaryPreferences;
    private String emergencyContact;

    // Constructor privado
    private PatientResponse() {}

    // Factory method
    public static PatientResponse fromPatient(Patient patient) {
        PatientResponse response = new PatientResponse();

        // Campos comunes
        response.userId = patient.getUserId().getValue();
        response.email = patient.getEmail().getValue();
        response.role = patient.getRole().getValue();
        response.firstName = patient.getFirstName();
        response.lastName = patient.getLastName();
        response.fullName = patient.getFullName();
        response.birthDate = patient.getBirthDate();
        response.phone = patient.getPhone();
        response.createdAt = patient.getCreatedAt();

        // Campos específicos de Patient
        response.height = patient.getHeight();
        response.weight = patient.getWeight();
        response.hasMedicalCondition = patient.hasMedicalCondition();
        response.chronicDisease = patient.getChronicDisease();
        response.allergies = patient.getAllergies();
        response.dietaryPreferences = patient.getDietaryPreferences();
        response.emergencyContact = patient.getEmergencyContact();

        // Calcular BMI si es posible
        if (patient.getHeight() != null && patient.getWeight() != null && patient.getHeight() > 0) {
            try {
                response.bmi = Math.round(patient.calculateBMI() * 100.0) / 100.0; // 2 decimales
                response.bmiCategory = calculateBMICategory(response.bmi);
            } catch (Exception e) {
                response.bmi = null;
                response.bmiCategory = null;
            }
        }

        return response;
    }

    private static String calculateBMICategory(Double bmi) {
        if (bmi == null) return null;

        if (bmi < 18.5) return "Bajo peso";
        else if (bmi < 25.0) return "Peso normal";
        else if (bmi < 30.0) return "Sobrepeso";
        else return "Obesidad";
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return fullName; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Double getHeight() { return height; }
    public Double getWeight() { return weight; }
    public Double getBmi() { return bmi; }
    public String getBmiCategory() { return bmiCategory; }
    public Boolean getHasMedicalCondition() { return hasMedicalCondition; }
    public String getChronicDisease() { return chronicDisease; }
    public String getAllergies() { return allergies; }
    public String getDietaryPreferences() { return dietaryPreferences; }
    public String getEmergencyContact() { return emergencyContact; }

    // Método para obtener edad calculada
    public Integer getAge() {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // Método para verificar si el perfil médico está completo
    public Boolean isMedicalProfileComplete() {
        return height != null && weight != null &&
                (hasMedicalCondition == null || !hasMedicalCondition ||
                        (chronicDisease != null && !chronicDisease.trim().isEmpty()));
    }
}
