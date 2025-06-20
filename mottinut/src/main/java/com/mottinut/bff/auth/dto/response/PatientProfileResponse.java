package com.mottinut.bff.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.mottinut.auth.domain.entities.Patient;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Base64;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("patient")
public class PatientProfileResponse extends UserProfileResponse {
    private Double height;
    private Double weight;
    private Boolean hasMedicalCondition;
    private String chronicDisease;
    private String allergies;
    private String dietaryPreferences;
    private String emergencyContact;
    private String gender;
    private String profileImageBase64; // Para enviar la imagen como base64
    private String imageContentType;

    public static PatientProfileResponse fromPatient(Patient patient) {
        PatientProfileResponse response = new PatientProfileResponse();
        response.setUserId(patient.getUserId().getValue());
        response.setEmail(patient.getEmail().getValue());
        response.setFirstName(patient.getFirstName());
        response.setLastName(patient.getLastName());
        response.setFullName(patient.getFullName());
        response.setBirthDate(patient.getBirthDate());
        response.setPhone(patient.getPhone());
        response.setRole(patient.getRole().getValue());
        response.setCreatedAt(patient.getCreatedAt());
        response.setHeight(patient.getHeight());
        response.setWeight(patient.getWeight());
        response.setHasMedicalCondition(patient.hasMedicalCondition());
        response.setChronicDisease(patient.getChronicDisease());
        response.setAllergies(patient.getAllergies());
        response.setDietaryPreferences(patient.getDietaryPreferences());
        response.setEmergencyContact(patient.getEmergencyContact());
        response.setGender(patient.getGender());

        response.setProfileImageBase64(patient.getProfileImage() != null ?
                Base64.getEncoder().encodeToString(patient.getProfileImage()) : null);
        response.setImageContentType(patient.getImageContentType());
        return response;
    }
}
