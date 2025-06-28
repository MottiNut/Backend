package com.mottinut.auth.domain.emalServices.services;

import com.mottinut.auth.domain.emalServices.valueObject.VerificationCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${verification.email.from}")
    private String fromEmail;

    @Value("${verification.email.company-name}")
    private String companyName;

    @Async
    public void sendVerificationCode(String toEmail, String userName, VerificationCode code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MULTIPART_MODE_MIXED,
                    UTF_8.name()
            );

            // Preparar variables para la plantilla
            Map<String, Object> properties = new HashMap<>();
            properties.put("username", userName);
            properties.put("verificationCode", code.getValue());
            properties.put("companyName", companyName);

            Context context = new Context();
            context.setVariables(properties);

            // Configurar el email
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🔐 Verificación de cuenta - " + companyName);

            // Procesar la plantilla con Thymeleaf
            String htmlContent = templateEngine.process("verification-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Código de verificación HTML enviado a: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Error enviando email de verificación a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Error enviando email de verificación", e);
        }
    }
}