package com.mottinut.auth.presentation.dto;

import com.mottinut.auth.domain.entities.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserResponse {
    private Long userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phone;
    private Double height;
    private Double weight;
    private boolean hasMedicalCondition;
    private String chronicDisease;
    private String allergies;
    private String dietaryPreferences;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.userId = user.getUserId().getValue();
        response.email = user.getEmail().getValue();
        response.role = user.getRole().getValue();
        response.firstName = user.getFirstName();
        response.lastName = user.getLastName();
        response.birthDate = user.getBirthDate();
        response.phone = user.getPhone();
        response.height = user.getHeight();
        response.weight = user.getWeight();
        response.hasMedicalCondition = user.hasMedicalCondition();
        response.chronicDisease = user.getChronicDisease();
        response.allergies = user.getAllergies();
        response.dietaryPreferences = user.getDietaryPreferences();
        response.createdAt = user.getCreatedAt();
        return response;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public boolean isHasMedicalCondition() { return hasMedicalCondition; }
    public void setHasMedicalCondition(boolean hasMedicalCondition) { this.hasMedicalCondition = hasMedicalCondition; }

    public String getChronicDisease() { return chronicDisease; }
    public void setChronicDisease(String chronicDisease) { this.chronicDisease = chronicDisease; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getDietaryPreferences() { return dietaryPreferences; }
    public void setDietaryPreferences(String dietaryPreferences) { this.dietaryPreferences = dietaryPreferences; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}