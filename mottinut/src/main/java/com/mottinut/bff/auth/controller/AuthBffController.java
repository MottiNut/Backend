package com.mottinut.bff.auth.controller;

import com.mottinut.auth.domain.emalServices.requestCode.ResendCodeRequest;
import com.mottinut.auth.domain.emalServices.requestCode.VerifyCodeRequest;
import com.mottinut.auth.domain.emalServices.responsiveStatus.VerificationStatusResponse;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.bff.auth.dto.request.*;
import com.mottinut.bff.auth.dto.response.AuthResponse;
import com.mottinut.bff.auth.dto.response.NutritionistProfileResponse;
import com.mottinut.bff.auth.dto.response.PatientProfileResponse;
import com.mottinut.bff.auth.dto.response.UserProfileResponse;
import com.mottinut.bff.auth.service.AuthBffService;
import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/bff/auth")
@CrossOrigin(origins = "*")
@Slf4j
public class AuthBffController {

    private final AuthBffService authBffService;
    private final UserService userService;

    public AuthBffController(AuthBffService authBffService, UserService userService) {
        this.authBffService = authBffService;
        this.userService = userService;
    }

    // ================ ENDPOINTS DE AUTENTICACIÓN ================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authBffService.login(request));
    }

    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponse> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        return ResponseEntity.ok(authBffService.registerPatient(request));
    }

    @PostMapping(value = "/register/nutritionist", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> registerNutritionist(
            @Valid @ModelAttribute RegisterNutritionistRequest request,
            @RequestParam(value = "profileImage", required = true) MultipartFile profileImage,
            @RequestParam(value = "licenseFrontImage", required = true) MultipartFile licenseFrontImage,
            @RequestParam(value = "licenseBackImage", required = true) MultipartFile licenseBackImage) {

        try {
            log.info("Iniciando registro de nutricionista - email: {}", request.getEmail());

            // Validar que los archivos no sean nulos antes de validar contenido
            if (profileImage == null || profileImage.isEmpty()) {
                log.error("Imagen de perfil faltante o vacía");
                throw new ValidationException("La imagen de perfil es requerida");
            }

            if (licenseFrontImage == null || licenseFrontImage.isEmpty()) {
                log.error("Imagen frontal de licencia faltante o vacía");
                throw new ValidationException("La imagen frontal de la licencia es requerida");
            }

            if (licenseBackImage == null || licenseBackImage.isEmpty()) {
                log.error("Imagen posterior de licencia faltante o vacía");
                throw new ValidationException("La imagen posterior de la licencia es requerida");
            }

            // Validar contenido de las imágenes
            validateImage(profileImage);
            validateLicenseImage(licenseFrontImage, "Imagen frontal de licencia");
            validateLicenseImage(licenseBackImage, "Imagen posterior de licencia");

            log.info("Validaciones de archivos completadas exitosamente");

            // Llamar al servicio
            AuthResponse response = authBffService.registerNutritionist(
                    request, profileImage, licenseFrontImage, licenseBackImage);

            log.info("Registro de nutricionista completado exitosamente");
            return ResponseEntity.ok(response);

        } catch (ValidationException e) {
            log.error("Error de validación en registro de nutricionista: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error interno en registro de nutricionista", e);
            throw new RuntimeException("Error interno del servidor: " + e.getMessage());
        }
    }

    // ================ ENDPOINTS DE VERIFICACIÓN ================

    @GetMapping("/verification/status")
    public ResponseEntity<VerificationStatusResponse> getVerificationStatus(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        log.info("Consultando estado de verificación para usuario: {}", principal.getUser().getUserId().getValue());
        return ResponseEntity.ok(authBffService.getVerificationStatus(principal.getUser().getUserId()));
    }

    @PostMapping("/verification/send/email")
    public ResponseEntity<VerificationStatusResponse> sendEmailVerification(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        log.info("Enviando verificación por email para usuario: {}", principal.getUser().getUserId().getValue());
        return ResponseEntity.ok(authBffService.sendEmailVerification(principal.getUser().getUserId()));
    }

    @PostMapping("/verification/send/sms")
    public ResponseEntity<VerificationStatusResponse> sendSmsVerification(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        log.info("Enviando verificación por SMS para usuario: {}", principal.getUser().getUserId().getValue());
        return ResponseEntity.ok(authBffService.sendSmsVerification(principal.getUser().getUserId()));
    }

    @PostMapping("/verification/send/whatsapp")
    public ResponseEntity<VerificationStatusResponse> sendWhatsAppVerification(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        log.info("Enviando verificación por WhatsApp para usuario: {}", principal.getUser().getUserId().getValue());
        return ResponseEntity.ok(authBffService.sendWhatsAppVerification(principal.getUser().getUserId()));
    }

    @PostMapping("/verification/resend")
    public ResponseEntity<VerificationStatusResponse> resendVerificationCode(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ResendCodeRequest request) {
        log.info("Reenviando código de verificación {} para usuario: {}",
                request.getType(), principal.getUser().getUserId().getValue());
        return ResponseEntity.ok(authBffService.resendVerificationCode(
                principal.getUser().getUserId(), request));
    }

    @PostMapping("/verification/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        try {
            VerificationStatusResponse response = authBffService.verifyCode(request);

            if (response.isEmailVerified()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (com.mottinut.shared.domain.exceptions.ValidationException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage(), "success", false)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Error interno del servidor", "success", false)
            );
        }
    }

    // ================ ENDPOINTS DE PERFIL EXISTENTES ================

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(authBffService.getCurrentUserProfile(principal.getUser()));
    }

    @PutMapping("/profile/patient")
    public ResponseEntity<PatientProfileResponse> updatePatientProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdatePatientProfileRequest request) {
        return ResponseEntity.ok(authBffService.updatePatientProfile(principal.getUser().getUserId(), request));
    }

    @PutMapping("/profile/nutritionist")
    public ResponseEntity<NutritionistProfileResponse> updateNutritionistProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdateNutritionistProfileRequest request) {
        return ResponseEntity.ok(authBffService.updateNutritionistProfile(principal.getUser().getUserId(), request));
    }

    @PostMapping(value = "/profile/patient/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PatientProfileResponse> updatePatientProfileImage(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam("image") MultipartFile image) {

        validateImage(image);
        return ResponseEntity.ok(authBffService.updatePatientProfileImage(
                principal.getUser().getUserId(), image));
    }

    @GetMapping("/profile/patient/{userId}/image")
    public ResponseEntity<byte[]> getPatientProfileImage(@PathVariable Long userId) {
        Patient patient = userService.getPatientById(new UserId(userId));

        if (patient.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(patient.getImageContentType()))
                .body(patient.getProfileImage());
    }

    @PostMapping(value = "/profile/nutritionist/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NutritionistProfileResponse> updateNutritionistProfileImage(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam("image") MultipartFile image) {

        validateImage(image);
        return ResponseEntity.ok(authBffService.updateNutritionistProfileImage(
                principal.getUser().getUserId(), image));
    }

    @GetMapping("/profile/nutritionist/{userId}/image")
    public ResponseEntity<byte[]> getNutritionistProfileImage(@PathVariable Long userId) {
        var nutritionist = userService.getNutritionistById(new UserId(userId));

        if (nutritionist.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(nutritionist.getImageContentType()))
                .body(nutritionist.getProfileImage());
    }

    // ================ MÉTODOS DE VALIDACIÓN ================

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ValidationException("La imagen no puede estar vacía");
        }

        if (image.getSize() > 5 * 1024 * 1024) { // 5MB máximo
            throw new ValidationException("La imagen no puede superar los 5MB");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("El archivo debe ser una imagen");
        }

        if (!isValidImageType(contentType)) {
            throw new ValidationException("Tipo de imagen no válido. Se permiten: JPG, JPEG, PNG, GIF");
        }
    }

    private void validateLicenseImage(MultipartFile image, String fieldName) {
        if (image == null || image.isEmpty()) {
            throw new ValidationException(fieldName + " es requerida");
        }

        if (image.getSize() > 10 * 1024 * 1024) { // 10MB máximo para licencias
            throw new ValidationException(fieldName + " no puede superar los 10MB");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException(fieldName + " debe ser una imagen");
        }

        if (!isValidImageType(contentType)) {
            throw new ValidationException(fieldName + " - Tipo de imagen no válido. Se permiten: JPG, JPEG, PNG, GIF");
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif");
    }


    /*Share profile*/
    @PostMapping("/share/generate-link")
    public ResponseEntity<Map<String, String>> generateShareLink(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            HttpServletRequest request) {

        log.info("Generando enlace de compartir para usuario: {}", principal.getUser().getUserId().getValue());

        // Construir URL base dinámicamente
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }

        Map<String, String> shareLinks = authBffService.generateShareLink(principal.getUser(), baseUrl);

        return ResponseEntity.ok(shareLinks);
    }

    @GetMapping("/share/patient/{userId}")
    public ResponseEntity<Map<String, Object>> getSharedPatientProfile(
            @PathVariable Long userId,
            HttpServletRequest request) {

        try {
            log.info("Accediendo a perfil compartido de paciente: {}", userId);

            Map<String, Object> profileData = authBffService.getPublicPatientProfile(new UserId(userId));

            return ResponseEntity.ok(profileData);
        } catch (ValidationException e) {
            log.warn("Perfil de paciente privado o no encontrado: {}", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage(), "success", false));
        } catch (Exception e) {
            log.error("Error obteniendo perfil compartido de paciente: {}", userId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/share/nutritionist/{userId}")
    public ResponseEntity<Map<String, Object>> getSharedNutritionistProfile(
            @PathVariable Long userId,
            HttpServletRequest request) {

        try {
            log.info("Accediendo a perfil compartido de nutricionista: {}", userId);

            Map<String, Object> profileData = authBffService.getPublicNutritionistProfile(new UserId(userId));

            return ResponseEntity.ok(profileData);
        } catch (ValidationException e) {
            log.warn("Perfil de nutricionista privado o no encontrado: {}", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage(), "success", false));
        } catch (Exception e) {
            log.error("Error obteniendo perfil compartido de nutricionista: {}", userId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/share/resolve/{shortCode}")
    public ResponseEntity<Map<String, Object>> resolveShortCode(
            @PathVariable String shortCode,
            HttpServletRequest request) {

        try {
            log.info("Resolviendo código corto: {}", shortCode);

            // Obtener información del request para analytics
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String referrer = request.getHeader("Referer");

            Map<String, Object> resolveData = authBffService.resolveShareCode(shortCode, ipAddress, userAgent, referrer);

            return ResponseEntity.ok(resolveData);
        } catch (ValidationException e) {
            log.warn("Código inválido o expirado: {}", shortCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage(), "success", false));
        } catch (NotFoundException e) {
            log.warn("Código no encontrado: {}", shortCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage(), "success", false));
        } catch (Exception e) {
            log.error("Error resolviendo código corto: {}", shortCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno del servidor", "success", false));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }


}

