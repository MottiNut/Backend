package com.mottinut.auth.domain.services;

import com.mottinut.auth.domain.emalServices.enums.VerificationType;
import com.mottinut.auth.domain.emalServices.requestCode.ResendCodeRequest;
import com.mottinut.auth.domain.emalServices.requestCode.VerifyCodeRequest;
import com.mottinut.auth.domain.emalServices.responsiveStatus.VerificationStatusResponse;
import com.mottinut.auth.domain.emalServices.services.VerificationService;
import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.factory.UserFactory;
import com.mottinut.auth.domain.repositories.UserRepository;
import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Token;
import com.mottinut.bff.auth.dto.request.*;
import com.mottinut.bff.auth.dto.response.AuthResponse;
import com.mottinut.bff.auth.dto.response.NutritionistProfileResponse;
import com.mottinut.bff.auth.dto.response.PatientProfileResponse;
import com.mottinut.bff.auth.dto.response.UserProfileResponse;
import com.mottinut.crosscutting.security.JwtTokenProvider;
import com.mottinut.shared.domain.exceptions.BusinessException;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Transactional
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final VerificationService verificationService;

    public AuthService(UserRepository userRepository, UserFactory userFactory, VerificationService verificationService) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.verificationService = verificationService;
    }

    public Patient registerPatient(Email email, String plainPassword, String firstName,
                                   String lastName, LocalDate birthDate, String phone,
                                   Double height, Double weight, boolean hasMedicalCondition,
                                   String chronicDisease, String allergies, String dietaryPreferences, String gender) {

        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("El email ya existe");
        }

        Password password = Password.fromPlainText(plainPassword);

        Patient patient = userFactory.createPatient(
                null, email, password, firstName, lastName, birthDate, phone,
                height, weight, hasMedicalCondition, chronicDisease, allergies, dietaryPreferences, gender
        );

        return (Patient) userRepository.save(patient);
    }

    public Nutritionist registerNutritionist(String firstName, String lastName,
                                             byte[] profileImage, String profileImageContentType,
                                             Email email, String phone, String plainPassword,
                                             String cnpCode, byte[] licenseFrontImage, byte[] licenseBackImage,
                                             String specialty, String masterDegree, String otherSpecialty,
                                             String location, String address, boolean acceptTerms) {

        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("El email ya existe");
        }

        Password password = Password.fromPlainText(plainPassword);

        Nutritionist nutritionist = userFactory.createNutritionist(
                null,
                email,
                password,
                firstName,
                lastName,
                phone,
                cnpCode,
                specialty,
                location,
                address,
                masterDegree,
                otherSpecialty,
                acceptTerms,
                profileImage,
                profileImageContentType,
                licenseFrontImage,
                licenseBackImage
        );

        Nutritionist savedNutritionist = (Nutritionist) userRepository.save(nutritionist);

        // Enviar verificación por email para nutricionistas
        verificationService.sendEmailVerification(savedNutritionist.getUserId());

        return savedNutritionist;
    }

    public User authenticate(Email email, String plainPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new ValidationException("Credenciales incorrectas");
        }

        User user = userOpt.get();
        if (!user.getPassword().matches(plainPassword)) {
            throw new ValidationException("Credenciales incorrectas");
        }

        // Verificar si el usuario está verificado (solo para nutricionistas)
        if (user.getRole().isNutritionist() && !user.isEmailVerified()) {
            throw new ValidationException("Debes verificar tu email antes de poder iniciar sesión");
        }

        return user;
    }

    public User findById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }

    public User findByEmail(String email) {

        Email emailObj = new Email(email);

        return userRepository.findByEmail(emailObj)
                .orElseThrow(() -> new com.mottinut.shared.domain.exceptions.ValidationException("Usuario no encontrado"));
    }

}