package com.mottinut.bff.auth.service;

import com.mottinut.auth.domain.emalServices.enums.VerificationType;
import com.mottinut.auth.domain.emalServices.requestCode.ResendCodeRequest;
import com.mottinut.auth.domain.emalServices.requestCode.VerifyCodeRequest;
import com.mottinut.auth.domain.emalServices.responsiveStatus.VerificationStatusResponse;
import com.mottinut.auth.domain.emalServices.services.VerificationService;
import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.AuthService;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.auth.domain.valueobjects.Token;
import com.mottinut.bff.auth.dto.request.*;
import com.mottinut.bff.auth.dto.response.AuthResponse;
import com.mottinut.bff.auth.dto.response.NutritionistProfileResponse;
import com.mottinut.bff.auth.dto.response.PatientProfileResponse;
import com.mottinut.bff.auth.dto.response.UserProfileResponse;
import com.mottinut.crosscutting.security.JwtTokenProvider;
import com.mottinut.shared.domain.exceptions.BusinessException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class AuthBffService {

    private final AuthService authService;
    private final UserService userService;
    private final VerificationService verificationService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthBffService(AuthService authService, UserService userService,
                          VerificationService verificationService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.userService = userService;
        this.verificationService = verificationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            User user = authService.authenticate(new Email(request.getEmail()), request.getPassword());
            Token token = jwtTokenProvider.generateToken(user.getUserId(), user.getRole());

            return AuthResponse.builder()
                    .token(token.getValue())
                    .userId(user.getUserId().getValue())
                    .role(user.getRole().getValue())
                    .fullName(user.getFullName())
                    .email(user.getEmail().getValue())
                    .emailVerified(user.isEmailVerified())
                    .phoneVerified(user.isPhoneVerified())
                    .fullyVerified(user.isFullyVerified())
                    .requiresVerification(!user.isFullyVerified())
                    .message(user.isFullyVerified() ? "Inicio de sesión exitoso" : "Debes completar la verificación")
                    .build();
        } catch (com.mottinut.shared.domain.exceptions.ValidationException e) {
            if (e.getMessage().contains("verificar tu email")) {
                // Usuario existe pero no está verificado
                throw new com.mottinut.shared.domain.exceptions.ValidationException("Tu cuenta no está verificada. Por favor verifica tu email antes de continuar.");
            }
            throw e;
        }
    }

    public AuthResponse registerPatient(RegisterPatientRequest request) {
        Patient patient = authService.registerPatient(
                new Email(request.getEmail()),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getPhone(),
                request.getHeight(),
                request.getWeight(),
                request.getHasMedicalCondition(),
                request.getChronicDisease(),
                request.getAllergies(),
                request.getDietaryPreferences(),
                request.getGender()
        );

        Token token = jwtTokenProvider.generateToken(patient.getUserId(), patient.getRole());

        return AuthResponse.builder()
                .token(token.getValue())
                .userId(patient.getUserId().getValue())
                .role(patient.getRole().getValue())
                .fullName(patient.getFullName())
                .email(patient.getEmail().getValue())
                .build();
    }

    public AuthResponse registerNutritionist(RegisterNutritionistRequest request,
                                             MultipartFile profileImage,
                                             MultipartFile licenseFrontImage,
                                             MultipartFile licenseBackImage) {
        try {
            // Validar que se aceptaron los términos y condiciones
            if (!request.isAcceptTerms()) {
                throw new com.mottinut.shared.domain.exceptions.ValidationException("Debe aceptar los términos y condiciones para continuar");
            }

            // Validar que las imágenes no sean nulas
            if (profileImage == null || profileImage.isEmpty()) {
                throw new com.mottinut.shared.domain.exceptions.ValidationException("La imagen de perfil es requerida");
            }
            if (licenseFrontImage == null || licenseFrontImage.isEmpty()) {
                throw new com.mottinut.shared.domain.exceptions.ValidationException("La imagen frontal de la colegiatura es requerida");
            }
            if (licenseBackImage == null || licenseBackImage.isEmpty()) {
                throw new com.mottinut.shared.domain.exceptions.ValidationException("La imagen trasera de la colegiatura es requerida");
            }

            byte[] profileImageBytes = profileImage.getBytes();
            String profileImageContentType = profileImage.getContentType();
            byte[] licenseFrontBytes = licenseFrontImage.getBytes();
            byte[] licenseBackBytes = licenseBackImage.getBytes();

            Nutritionist nutritionist = authService.registerNutritionist(
                    request.getFirstName(),
                    request.getLastName(),
                    profileImageBytes,
                    profileImageContentType,
                    new Email(request.getEmail()),
                    request.getPhone(),
                    request.getPassword(),
                    request.getCnpCode(),
                    licenseFrontBytes,
                    licenseBackBytes,
                    request.getSpecialty(),
                    request.getMasterDegree(), // Puede ser null
                    request.getOtherSpecialty(), // Puede ser null
                    request.getLocation(),
                    request.getAddress(),
                    request.isAcceptTerms()
            );

            Token token = jwtTokenProvider.generateToken(nutritionist.getUserId(), nutritionist.getRole());

            return AuthResponse.builder()
                    .token(token.getValue())
                    .userId(nutritionist.getUserId().getValue())
                    .role(nutritionist.getRole().getValue())
                    .fullName(nutritionist.getFullName())
                    .email(nutritionist.getEmail().getValue())
                    .emailVerified(nutritionist.isEmailVerified())
                    .phoneVerified(nutritionist.isPhoneVerified())
                    .fullyVerified(nutritionist.isFullyVerified())
                    .requiresVerification(true)
                    .message("Registro exitoso. Te hemos enviado un código de verificación a tu email.")
                    .build();

        } catch (IOException e) {
            throw new com.mottinut.shared.domain.exceptions.ValidationException("Error al procesar imágenes");
        }
    }

    // ================ MÉTODOS DE VERIFICACIÓN ================

    public VerificationStatusResponse sendEmailVerification(UserId userId) {
        try {
            verificationService.sendEmailVerification(userId);
            User user = authService.findById(userId);

            return VerificationStatusResponse.builder()
                    .emailVerified(user.isEmailVerified())
                    .phoneVerified(user.isPhoneVerified())
                    .fullyVerified(user.isFullyVerified())
                    .message("Código de verificación enviado a tu email")
                    .build();
        } catch (Exception e) {
            throw new BusinessException("Error enviando código de verificación: " + e.getMessage());
        }
    }

    public VerificationStatusResponse sendSmsVerification(UserId userId) {
        try {
            verificationService.sendSmsVerification(userId);
            User user = authService.findById(userId);

            return VerificationStatusResponse.builder()
                    .emailVerified(user.isEmailVerified())
                    .phoneVerified(user.isPhoneVerified())
                    .fullyVerified(user.isFullyVerified())
                    .message("Código de verificación enviado por SMS")
                    .build();
        } catch (Exception e) {
            throw new BusinessException("Error enviando SMS: " + e.getMessage());
        }
    }

    public VerificationStatusResponse sendWhatsAppVerification(UserId userId) {
        try {
            verificationService.sendWhatsAppVerification(userId);
            User user = authService.findById(userId);

            return VerificationStatusResponse.builder()
                    .emailVerified(user.isEmailVerified())
                    .phoneVerified(user.isPhoneVerified())
                    .fullyVerified(user.isFullyVerified())
                    .message("Código de verificación enviado por WhatsApp")
                    .build();
        } catch (Exception e) {
            throw new BusinessException("Error enviando WhatsApp: " + e.getMessage());
        }
    }

    public VerificationStatusResponse resendVerificationCode(UserId userId, ResendCodeRequest request) {
        VerificationType type = VerificationType.valueOf(request.getType().toUpperCase());

        switch (type) {
            case EMAIL:
                return sendEmailVerification(userId);
            case SMS:
                return sendSmsVerification(userId);
            case WHATSAPP:
                return sendWhatsAppVerification(userId);
            default:
                throw new com.mottinut.shared.domain.exceptions.ValidationException("Tipo de verificación no válido");
        }
    }

    public VerificationStatusResponse verifyCode(VerifyCodeRequest request) {
        try {
            // 1. Obtener el usuario por email
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new com.mottinut.shared.domain.exceptions.ValidationException("El email es requerido para la verificación");
            }

            User user = authService.findByEmail(request.getEmail());
            if (user == null) {
                throw new com.mottinut.shared.domain.exceptions.ValidationException("Usuario no encontrado");
            }

            UserId userId = user.getUserId(); // Obtener el UserId del usuario encontrado

            // 2. Verificar el código
            VerificationType type = VerificationType.valueOf(request.getType().toUpperCase());
            boolean isVerified = verificationService.verifyCode(userId, type, request.getCode());

            if (!isVerified) {
                throw new com.mottinut.shared.domain.exceptions.ValidationException("Código de verificación incorrecto o expirado");
            }

            // 3. Actualizar el usuario para obtener el estado más reciente
            user = authService.findById(userId);
            String message = "Verificación exitosa";

            if (user.isFullyVerified()) {
                message = "¡Felicidades! Tu cuenta está completamente verificada";
            } else if (type == VerificationType.EMAIL) {
                message = "Email verificado exitosamente";
            } else {
                message = "Teléfono verificado exitosamente";
            }

            return VerificationStatusResponse.builder()
                    .emailVerified(user.isEmailVerified())
                    .phoneVerified(user.isPhoneVerified())
                    .fullyVerified(user.isFullyVerified())
                    .emailVerifiedAt(user.getEmailVerifiedAt() != null ?
                            user.getEmailVerifiedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                    .phoneVerifiedAt(user.getPhoneVerifiedAt() != null ?
                            user.getPhoneVerifiedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                    .message(message)
                    .build();

        } catch (IllegalArgumentException e) {
            throw new com.mottinut.shared.domain.exceptions.ValidationException("Tipo de verificación no válido");
        }
    }

    public VerificationStatusResponse getVerificationStatus(UserId userId) {
        User user = authService.findById(userId);

        String message = "Estado de verificación";
        if (user.isFullyVerified()) {
            message = "Tu cuenta está completamente verificada";
        } else if (user.isEmailVerified() && !user.isPhoneVerified()) {
            message = "Email verificado. Falta verificar teléfono";
        } else if (!user.isEmailVerified() && user.isPhoneVerified()) {
            message = "Teléfono verificado. Falta verificar email";
        } else {
            message = "Cuenta sin verificar. Verifica tu email y teléfono";
        }

        return VerificationStatusResponse.builder()
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .fullyVerified(user.isFullyVerified())
                .emailVerifiedAt(user.getEmailVerifiedAt() != null ?
                        user.getEmailVerifiedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .phoneVerifiedAt(user.getPhoneVerifiedAt() != null ?
                        user.getPhoneVerifiedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .message(message)
                .build();
    }

    // ================ MÉTODOS EXISTENTES ================

    public UserProfileResponse getCurrentUserProfile(User user) {
        if (user.getRole().isPatient()) {
            Patient patient = userService.getPatientById(user.getUserId());
            return PatientProfileResponse.fromPatient(patient);
        } else if (user.getRole().isNutritionist()) {
            Nutritionist nutritionist = userService.getNutritionistById(user.getUserId());
            return NutritionistProfileResponse.fromNutritionist(nutritionist);
        }
        throw new com.mottinut.shared.domain.exceptions.ValidationException("Tipo de usuario no válido");
    }

    public PatientProfileResponse updatePatientProfileImage(UserId userId, MultipartFile image) {
        try {
            byte[] imageData = image.getBytes();
            String contentType = image.getContentType();

            Patient updatedPatient = userService.updatePatientProfileImage(userId, imageData, contentType);
            return PatientProfileResponse.fromPatient(updatedPatient);
        } catch (IOException e) {
            throw new com.mottinut.shared.domain.exceptions.ValidationException("Error al procesar la imagen");
        }
    }

    public PatientProfileResponse updatePatientProfile(UserId userId, @Valid UpdatePatientProfileRequest request) {
        Patient updatedPatient = userService.updatePatientProfile(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getHeight(),
                request.getWeight(),
                request.getHasMedicalCondition(),
                request.getChronicDisease(),
                request.getAllergies(),
                request.getDietaryPreferences(),
                request.getEmergencyContact(),
                request.getGender()
        );

        return PatientProfileResponse.fromPatient(updatedPatient);
    }

    public NutritionistProfileResponse updateNutritionistProfile(UserId userId, @Valid UpdateNutritionistProfileRequest request) {
        Nutritionist updatedNutritionist = userService.updateNutritionistProfile(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getYearsOfExperience(),
                request.getBiography()
        );

        return NutritionistProfileResponse.fromNutritionist(updatedNutritionist);
    }

    public NutritionistProfileResponse updateNutritionistProfileImage(UserId userId, MultipartFile image) {
        try {
            byte[] imageData = image.getBytes();
            String contentType = image.getContentType();

            Nutritionist updatedNutritionist = userService.updateNutritionistProfileImage(userId, imageData, contentType);
            return NutritionistProfileResponse.fromNutritionist(updatedNutritionist);
        } catch (IOException e) {
            throw new com.mottinut.shared.domain.exceptions.ValidationException("Error al procesar la imagen");
        }
    }
}