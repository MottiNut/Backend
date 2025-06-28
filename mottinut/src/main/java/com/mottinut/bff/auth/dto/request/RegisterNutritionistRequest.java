package com.mottinut.bff.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterNutritionistRequest {

    @NotBlank(message = "El nombre es requerido")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    private String lastName;

    @NotNull(message = "La imagen de perfil es requerida")
    private MultipartFile profileImage;

    @NotBlank(message = "El email es requerido")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El número de teléfono es requerido")
    private String phone;

    @NotBlank(message = "El código CNP es requerido")
    private String cnpCode;

    @NotNull(message = "La imagen frontal de la colegiatura es requerida")
    private MultipartFile licenseFrontImage;

    @NotNull(message = "La imagen trasera de la colegiatura es requerida")
    private MultipartFile licenseBackImage;

    @NotBlank(message = "La especialidad es requerida")
    private String specialty;

    // Campos opcionales
    private String masterDegree;
    private String otherSpecialty;

    @NotBlank(message = "La ubicación es requerida")
    private String location;

    @NotBlank(message = "La dirección es requerida")
    private String address;

    @AssertTrue(message = "Debe aceptar los términos y condiciones")
    private boolean acceptTerms;
}
