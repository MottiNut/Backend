package com.mottinut.auth.domain.services;

import com.mottinut.auth.domain.entities.*;
import com.mottinut.auth.domain.repositories.ShareCodeRepository;
import com.mottinut.auth.domain.repositories.ShareClickRepository;
import com.mottinut.auth.domain.repositories.ProfilePrivacySettingsRepository;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ShareProfileService {

    private final ShareCodeRepository shareCodeRepository;
    private final ShareClickRepository shareClickRepository;
    private final ProfilePrivacySettingsRepository privacySettingsRepository;
    private final UserService userService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public ShareProfileService(ShareCodeRepository shareCodeRepository,
                               ShareClickRepository shareClickRepository,
                               ProfilePrivacySettingsRepository privacySettingsRepository,
                               UserService userService) {
        this.shareCodeRepository = shareCodeRepository;
        this.shareClickRepository = shareClickRepository;
        this.privacySettingsRepository = privacySettingsRepository;
        this.userService = userService;
    }

    /**
     * Genera o retorna el enlace de compartir para un usuario
     */
    public Map<String, String> generateShareLink(UserId userId, ShareCode.UserType userType, String baseUrl) {
        log.info("Generando enlace de compartir para usuario: {} tipo: {}", userId.getValue(), userType);

        // Verificar si ya existe un código activo
        Optional<ShareCode> existingCode = shareCodeRepository.findActiveByUserIdAndType(userId.getValue(), userType);

        ShareCode shareCode;
        if (existingCode.isPresent() && existingCode.get().isValid()) {
            shareCode = existingCode.get();
            log.info("Usando código existente: {}", shareCode.getShortCode());
        } else {
            // Crear nuevo código
            shareCode = createNewShareCode(userId, userType);
            log.info("Nuevo código creado: {}", shareCode.getShortCode());
        }

        // Construir URLs
        String directUrl = String.format("%s/profile/%s/%d",
                baseUrl, userType.name().toLowerCase(), userId.getValue());
        String shortUrl = String.format("%s/p/%s", baseUrl, shareCode.getShortCode());
        String qrCodeUrl = String.format("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=%s", shortUrl);

        Map<String, String> response = new HashMap<>();
        response.put("directUrl", directUrl);
        response.put("shortUrl", shortUrl);
        response.put("qrCodeUrl", qrCodeUrl);
        response.put("shortCode", shareCode.getShortCode());

        return response;
    }

    /**
     * Resuelve un código corto y retorna información del perfil
     */
    public Map<String, Object> resolveShortCode(String shortCode, String ipAddress, String userAgent, String referrer) {
        log.info("Resolviendo código corto: {}", shortCode);

        ShareCode shareCode = shareCodeRepository.findByShortCodeAndIsActiveTrue(shortCode)
                .orElseThrow(() -> new NotFoundException("Enlace no encontrado o expirado"));

        if (!shareCode.isValid()) {
            throw new ValidationException("El enlace ha expirado");
        }

        // Registrar el clic
        shareCode.incrementClickCount();
        shareCodeRepository.save(shareCode);

        // Registrar analytics del clic
        ShareClick shareClick = new ShareClick(shareCode.getId(), ipAddress, userAgent, referrer);
        shareClickRepository.save(shareClick);

        log.info("Código resuelto: usuario {} tipo {}", shareCode.getUserId(), shareCode.getUserType());

        // Retornar información básica para redirección
        Map<String, Object> result = new HashMap<>();
        result.put("userId", shareCode.getUserId().getValue());
        result.put("userType", shareCode.getUserType().name().toLowerCase());

        return result;
    }

    /**
     * Obtiene información pública del perfil de un paciente
     */
    public Map<String, Object> getPublicPatientProfile(UserId userId) {
        log.info("Obteniendo perfil público de paciente: {}", userId.getValue());

        Patient patient = userService.getPatientById(userId);
        ProfilePrivacySettings privacy = getOrCreatePrivacySettings(userId);

        if (!privacy.getIsProfilePublic()) {
            throw new ValidationException("Este perfil es privado");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", patient.getUserId().getValue());
        profile.put("firstName", patient.getFullName());
        profile.put("lastName", patient.getLastName());
        profile.put("profileImageUrl", "/api/bff/auth/profile/patient/" + userId.getValue() + "/image");
        profile.put("memberSince", patient.getCreatedAt());
        profile.put("profileType", "PATIENT");
        profile.put("isPublic", true);

        // Información opcional basada en privacidad
        if (privacy.getShowContactInfo()) {
            profile.put("phone", patient.getPhone());
        }

        if (privacy.getShowLocation()) {
            // Agregar ubicación si está disponible
        }

        profile.put("allowDirectMessages", privacy.getAllowDirectMessages());

        return profile;
    }

    /**
     * Obtiene información pública del perfil de un nutricionista
     */
    public Map<String, Object> getPublicNutritionistProfile(UserId userId) {
        log.info("Obteniendo perfil público de nutricionista: {}", userId.getValue());

        Nutritionist nutritionist = userService.getNutritionistById(userId);
        ProfilePrivacySettings privacy = getOrCreatePrivacySettings(userId);

        if (!privacy.getIsProfilePublic()) {
            throw new ValidationException("Este perfil es privado");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", nutritionist.getUserId().getValue());
        profile.put("firstName", nutritionist.getFirstName());
        profile.put("lastName", nutritionist.getLastName());
        profile.put("profileImageUrl", "/api/bff/auth/profile/nutritionist/" + userId.getValue() + "/image");
        profile.put("specialization", nutritionist.getSpecialty());
        profile.put("experience", nutritionist.getYearsOfExperience());
        profile.put("biography", nutritionist.getBiography());
        profile.put("memberSince", nutritionist.getCreatedAt());
        profile.put("profileType", "NUTRITIONIST");
        profile.put("isPublic", true);
        profile.put("isVerified", nutritionist.isFullyVerified());

        // Información opcional basada en privacidad
        if (privacy.getShowContactInfo()) {
            profile.put("phone", nutritionist.getPhone());
        }

        if (privacy.getShowLocation()) {
            profile.put("location", nutritionist.getLocation());
        }

        profile.put("allowDirectMessages", privacy.getAllowDirectMessages());

        return profile;
    }

    /**
     * Actualiza configuración de privacidad
     */
    public ProfilePrivacySettings updatePrivacySettings(UserId userId, boolean isProfilePublic,
                                                        boolean showContactInfo, boolean showLocation,
                                                        boolean allowDirectMessages) {
        ProfilePrivacySettings privacy = getOrCreatePrivacySettings(userId);

        privacy.setIsProfilePublic(isProfilePublic);
        privacy.setShowContactInfo(showContactInfo);
        privacy.setShowLocation(showLocation);
        privacy.setAllowDirectMessages(allowDirectMessages);

        return privacySettingsRepository.save(privacy);
    }

    /**
     * Obtiene configuración de privacidad actual
     */
    public ProfilePrivacySettings getPrivacySettings(UserId userId) {
        return getOrCreatePrivacySettings(userId);
    }

    /**
     * Obtiene estadísticas de enlaces compartidos
     */
    public Map<String, Object> getShareStatistics(UserId userId, ShareCode.UserType userType) {
        Optional<ShareCode> shareCodeOpt = shareCodeRepository.findActiveByUserIdAndType(userId.getValue(), userType);

        Map<String, Object> stats = new HashMap<>();

        if (shareCodeOpt.isPresent()) {
            ShareCode shareCode = shareCodeOpt.get();
            long totalClicks = shareClickRepository.countByShareCodeId(shareCode.getId());

            stats.put("hasShareLink", true);
            stats.put("shortCode", shareCode.getShortCode());
            stats.put("totalClicks", totalClicks);
            stats.put("createdAt", shareCode.getCreatedAt());
            stats.put("expiresAt", shareCode.getExpiresAt());
        } else {
            stats.put("hasShareLink", false);
            stats.put("totalClicks", 0);
        }

        return stats;
    }

    // ================ MÉTODOS PRIVADOS ================

    private ShareCode createNewShareCode(UserId userId, ShareCode.UserType userType) {
        String shortCode;
        int attempts = 0;

        do {
            shortCode = generateRandomCode();
            attempts++;

            if (attempts > 10) {
                throw new ValidationException("No se pudo generar un código único");
            }
        } while (shareCodeRepository.existsByShortCode(shortCode));

        ShareCode shareCode = new ShareCode(shortCode, userId, userType);
        return shareCodeRepository.save(shareCode);
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return "MTN" + code.toString(); // Prefijo para identificar la app
    }

    private ProfilePrivacySettings getOrCreatePrivacySettings(UserId userId) {
        return privacySettingsRepository.findByUserId(userId.getValue())
                .orElseGet(() -> {
                    ProfilePrivacySettings newSettings = new ProfilePrivacySettings(userId);
                    return privacySettingsRepository.save(newSettings);
                });
    }
}
