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

    // ✅ Método principal con imagen
    public Patient createPatient(UserId userId, Email email, Password password, String firstName,
                                 String lastName, LocalDate birthDate, String phone, Double height,
                                 Double weight, boolean hasMedicalCondition, String chronicDisease,
                                 String allergies, String dietaryPreferences, String gender,
                                 byte[] profileImage, String imageContentType) {
        return new Patient(userId, email, password, firstName, lastName, birthDate, phone,
                height, weight, hasMedicalCondition, chronicDisease, allergies,
                dietaryPreferences, null, gender, profileImage, imageContentType);
    }

    // ✅ Método sobrecargado sin imagen (para casos donde no se proporciona imagen)
    public Patient createPatient(UserId userId, Email email, Password password, String firstName,
                                 String lastName, LocalDate birthDate, String phone, Double height,
                                 Double weight, boolean hasMedicalCondition, String chronicDisease,
                                 String allergies, String dietaryPreferences, String gender) {
        return createPatient(userId, email, password, firstName, lastName, birthDate, phone,
                height, weight, hasMedicalCondition, chronicDisease, allergies,
                dietaryPreferences, gender, null, null);
    }

    public Nutritionist createNutritionist(UserId userId, Email email, Password password,
                                           String firstName, String lastName, String phone,
                                           String cnpCode, String specialty, String location,
                                           byte[] profileImage, String profileImageContentType,
                                           byte[] licenseFrontImage, byte[] licenseBackImage) {

        LocalDate defaultBirthDate = LocalDate.now().minusYears(25);

        return new Nutritionist(
                userId,
                email,
                password,
                firstName,
                lastName,
                defaultBirthDate,
                phone,
                profileImage,
                profileImageContentType,
                cnpCode,
                licenseFrontImage,
                licenseBackImage,
                specialty,
                null, // masterDegree
                null, // otherSpecialty
                location,
                null, // address
                true  // acceptTerms
        );
    }

    // Método sobrecargado con todos los parámetros opcionales
    public Nutritionist createNutritionist(UserId userId, Email email, Password password,
                                           String firstName, String lastName, String phone,
                                           String cnpCode, String specialty, String location, String address,
                                           String masterDegree, String otherSpecialty, boolean acceptTerms,
                                           byte[] profileImage, String profileImageContentType,
                                           byte[] licenseFrontImage, byte[] licenseBackImage) {

        LocalDate defaultBirthDate = LocalDate.now().minusYears(25);

        return new Nutritionist(
                userId,
                email,
                password,
                firstName,
                lastName,
                defaultBirthDate,
                phone,
                profileImage,
                profileImageContentType,
                cnpCode,
                licenseFrontImage,
                licenseBackImage,
                specialty,
                masterDegree,
                otherSpecialty,
                location,
                address,
                acceptTerms
        );
    }
}