package com.mottinut.auth.domain.emalServices.services;

import com.mottinut.auth.domain.emalServices.entity.UserVerification;
import com.mottinut.auth.domain.emalServices.enums.VerificationType;
import com.mottinut.auth.domain.emalServices.repositories.UserVerificationRepository;
import com.mottinut.auth.domain.emalServices.valueObject.VerificationCode;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.repositories.UserRepository;
import com.mottinut.shared.domain.exceptions.BusinessException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final UserVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TwilioService twilioService;

    @Value("${verification.code.expiration-minutes}")
    private int codeExpirationMinutes;

    public void sendEmailVerification(UserId userId) {
        User user = getUserById(userId);

        // Verificar si ya puede reenviar
        checkCanResend(userId, VerificationType.EMAIL);

        VerificationCode code = VerificationCode.generate();
        UserVerification verification = new UserVerification(
                userId, code, VerificationType.EMAIL,
                user.getEmail().getValue(), codeExpirationMinutes
        );

        verificationRepository.save(verification);
        emailService.sendVerificationCode(
                user.getEmail().getValue(),
                user.getFullName(),
                code
        );

        log.info("Código de verificación por email enviado para usuario: {}", userId.getValue());
    }

    public void sendSmsVerification(UserId userId) {
        User user = getUserById(userId);

        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new ValidationException("El usuario no tiene número de teléfono registrado");
        }

        checkCanResend(userId, VerificationType.SMS);

        VerificationCode code = VerificationCode.generate();
        UserVerification verification = new UserVerification(
                userId, code, VerificationType.SMS,
                user.getPhone(), codeExpirationMinutes
        );

        verificationRepository.save(verification);
        twilioService.sendSmsVerificationCode(user.getPhone(), user.getFullName(), code);

        log.info("Código de verificación por SMS enviado para usuario: {}", userId.getValue());
    }

    public void sendWhatsAppVerification(UserId userId) {
        User user = getUserById(userId);

        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new ValidationException("El usuario no tiene número de teléfono registrado");
        }

        checkCanResend(userId, VerificationType.WHATSAPP);

        VerificationCode code = VerificationCode.generate();
        UserVerification verification = new UserVerification(
                userId, code, VerificationType.WHATSAPP,
                user.getPhone(), codeExpirationMinutes
        );

        verificationRepository.save(verification);
        twilioService.sendWhatsAppVerificationCode(user.getPhone(), user.getFullName(), code);

        log.info("Código de verificación por WhatsApp enviado para usuario: {}", userId.getValue());
    }

    private void checkCanResend(UserId userId, VerificationType type) {
        // Usar LocalDateTime.now() como parámetro
        boolean hasActivePending = verificationRepository.existsByUserIdAndTypeAndNotExpired(
                userId, type, LocalDateTime.now()
        );

        if (hasActivePending) {
            throw new ValidationException("Debes esperar antes de solicitar un nuevo código");
        }
    }

    public boolean verifyCode(UserId userId, VerificationType type, String codeValue) {
        VerificationCode providedCode = VerificationCode.from(codeValue);

        Optional<UserVerification> verificationOpt =
                verificationRepository.findValidVerification(userId, type, codeValue, LocalDateTime.now());

        if (verificationOpt.isEmpty()) {
            throw new BusinessException("Código de verificación inválido o expirado");
        }

        UserVerification verification = verificationOpt.get();

        if (verification.verify(providedCode)) {
            // Actualizar el usuario según el tipo de verificación
            User user = getUserById(userId);

            if (type == VerificationType.EMAIL) {
                user.verifyEmail();
            } else if (type == VerificationType.SMS || type == VerificationType.WHATSAPP) {
                user.verifyPhone();
            }

            userRepository.save(user);

            // Marcar como verificado
            verification.markAsVerified();
            verificationRepository.save(verification);

            log.info("Usuario {} verificado exitosamente por {}", userId.getValue(), type);
            return true;
        }

        return false;
    }


    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredVerifications() {
        verificationRepository.deleteExpiredVerifications(LocalDateTime.now());
    }

    private User getUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }

}

