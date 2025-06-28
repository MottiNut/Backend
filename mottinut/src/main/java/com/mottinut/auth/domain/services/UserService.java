package com.mottinut.auth.domain.services;

import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.repositories.UserRepository;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    public Patient getPatientById(UserId userId) {
        return userRepository.findPatientById(userId)
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado"));
    }

    public Nutritionist getNutritionistById(UserId userId) {
        return userRepository.findNutritionistById(userId)
                .orElseThrow(() -> new NotFoundException("Nutricionista no encontrado"));
    }

    public Patient updatePatientProfile(UserId userId, String firstName, String lastName, String phone,
                                        Double height, Double weight, boolean hasMedicalCondition,
                                        String chronicDisease, String allergies, String dietaryPreferences,
                                        String emergencyContact, String gender) {
        Patient patient = getPatientById(userId);
        patient.updateBasicProfile(firstName, lastName, phone);
        patient.updateMedicalProfile(height, weight, hasMedicalCondition, chronicDisease,
                allergies, dietaryPreferences, emergencyContact, gender);
        return (Patient) userRepository.save(patient);
    }

    public Patient updatePatientProfileImage(UserId userId, byte[] imageData, String contentType) {
        Patient patient = getPatientById(userId);
        patient.updateProfileImage(imageData, contentType);
        return (Patient) userRepository.save(patient);
    }

    public Nutritionist updateNutritionistProfile(UserId userId, String firstName, String lastName,
                                                  String phone, Integer yearsOfExperience, String biography) {
        Nutritionist nutritionist = getNutritionistById(userId);
        nutritionist.updateBasicProfile(firstName, lastName, phone);
        nutritionist.updateProfessionalProfile(yearsOfExperience, biography);
        return (Nutritionist) userRepository.save(nutritionist);
    }

    public Nutritionist updateNutritionistProfileImage(UserId userId, byte[] imageData, String contentType) {
        Nutritionist nutritionist = getNutritionistById(userId);
        nutritionist.updateProfileImage(imageData, contentType);
        return (Nutritionist) userRepository.save(nutritionist);
    }
}