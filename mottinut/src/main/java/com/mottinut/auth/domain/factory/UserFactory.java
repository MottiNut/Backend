package com.mottinut.auth.domain.factory;

import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserFactory {

    public Patient createPatient(UserId userId, Email email, Password password,
                                 String firstName, String lastName, LocalDate birthDate, String phone,
                                 Double height, Double weight, boolean hasMedicalCondition,
                                 String chronicDisease, String allergies, String dietaryPreferences, String gender) {
        return new Patient(userId, email, password, firstName, lastName, birthDate, phone,
                height, weight, hasMedicalCondition, chronicDisease, allergies,
                dietaryPreferences, null, gender); //
    }

    public Nutritionist createNutritionist(UserId userId, Email email, Password password,
                                           String firstName, String lastName, LocalDate birthDate, String phone,
                                           String licenseNumber, String specialization, String workplace) {
        return new Nutritionist(userId, email, password, firstName, lastName, birthDate, phone,
                licenseNumber, specialization, workplace, null, null); // opcional
    }
}