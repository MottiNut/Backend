package com.mottinut.auth.domain.emalServices.services;

import com.mottinut.auth.domain.emalServices.valueObject.VerificationCode;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class TwilioService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    @Value("${twilio.whatsapp-number}")
    private String fromWhatsAppNumber;

    @Value("${verification.email.company-name}")
    private String companyName;

    @Value("${twilio.trial-mode:false}")
    private boolean trialMode;

    // Patrón para validar números peruanos
    private static final Pattern PERU_PHONE_PATTERN = Pattern.compile("^\\+51[0-9]{9}$");

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio inicializado con número pagado: {}. Modo trial: {}", fromPhoneNumber, trialMode);
    }

    /**
     * Envía SMS de verificación - Ahora siempre usa el número pagado
     */
    public void sendSmsVerificationCode(String toPhoneNumber, String userName, VerificationCode code) {
        try {
            // Validar formato del número
            validatePhoneNumber(toPhoneNumber);

            String messageBody = String.format(
                    "Hola %s! Tu código de verificación para %s es: %s. Expira en 10 minutos.",
                    userName, companyName, code.getValue()
            );

            // Siempre usar el número pagado para envíos
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromPhoneNumber), // Tu número de EE.UU.
                    messageBody
            ).create();

            log.info("SMS enviado exitosamente desde {} a {}: SID {}",
                    fromPhoneNumber, toPhoneNumber, message.getSid());

        } catch (Exception e) {
            log.error("Error enviando SMS desde {} a {}: {}",
                    fromPhoneNumber, toPhoneNumber, e.getMessage());
            throw new RuntimeException("Error enviando SMS de verificación: " + e.getMessage(), e);
        }
    }

    /**
     * Envía WhatsApp de verificación
     */
    public void sendWhatsAppVerificationCode(String toPhoneNumber, String userName, VerificationCode code) {
        try {
            validatePhoneNumber(toPhoneNumber);

            String messageBody = String.format(
                    "🔐 *%s - Verificación*\n\n" +
                            "Hola %s!\n\n" +
                            "Tu código de verificación es: *%s*\n\n" +
                            "⏰ Expira en 10 minutos.\n\n" +
                            "Si no solicitaste este código, ignora este mensaje.",
                    companyName, userName, code.getValue()
            );

            // Formatear número para WhatsApp
            String whatsappToNumber = "whatsapp:" + toPhoneNumber;

            Message message = Message.creator(
                    new PhoneNumber(whatsappToNumber),
                    new PhoneNumber(fromWhatsAppNumber), // Sandbox de WhatsApp
                    messageBody
            ).create();

            log.info("WhatsApp enviado exitosamente a {}: SID {}",
                    toPhoneNumber, message.getSid());

        } catch (Exception e) {
            log.error("Error enviando WhatsApp a {}: {}", toPhoneNumber, e.getMessage());
            throw new RuntimeException("Error enviando WhatsApp de verificación: " + e.getMessage(), e);
        }
    }

    /**
     * Valida el formato del número de teléfono peruano
     */
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono no puede estar vacío");
        }

        // Normalizar el número si viene sin +51
        String normalizedNumber = normalizePeruvianNumber(phoneNumber);

        if (!PERU_PHONE_PATTERN.matcher(normalizedNumber).matches()) {
            throw new IllegalArgumentException(
                    "Formato de número peruano inválido. Debe ser +51XXXXXXXXX (9 dígitos después de +51)"
            );
        }
    }

    /**
     * Normaliza números peruanos agregando +51 si es necesario
     */
    private String normalizePeruvianNumber(String phoneNumber) {
        String cleaned = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");

        // Si ya tiene +51, devolverlo como está
        if (cleaned.startsWith("+51")) {
            return cleaned;
        }

        // Si empieza con 51, agregar +
        if (cleaned.startsWith("51") && cleaned.length() == 11) {
            return "+" + cleaned;
        }

        // Si es un número local de 9 dígitos, agregar +51
        if (cleaned.length() == 9 && cleaned.matches("^[0-9]{9}$")) {
            return "+51" + cleaned;
        }

        return cleaned;
    }

    /**
     * Método de prueba para enviar SMS directo
     */
    public String sendTestSMS(String toNumber, String message) {
        try {
            validatePhoneNumber(toNumber);
            String normalizedNumber = normalizePeruvianNumber(toNumber);

            Message twilioMessage = Message.creator(
                    new PhoneNumber(normalizedNumber),
                    new PhoneNumber(fromPhoneNumber),
                    message + "\n\n[Mensaje de prueba]"
            ).create();

            String result = String.format(
                    "SMS de prueba enviado exitosamente desde %s a %s. SID: %s",
                    fromPhoneNumber, normalizedNumber, twilioMessage.getSid()
            );

            log.info(result);
            return result;

        } catch (Exception e) {
            String error = "Error enviando SMS de prueba: " + e.getMessage();
            log.error(error);
            return error;
        }
    }

    /**
     * Obtiene información del servicio actual
     */
    public String getServiceInfo() {
        return String.format(
                "Servicio Twilio configurado:\n" +
                        "- Número SMS: %s\n" +
                        "- Número WhatsApp: %s\n" +
                        "- Modo Trial: %s\n" +
                        "- Cuenta: %s",
                fromPhoneNumber, fromWhatsAppNumber, trialMode,
                accountSid.substring(0, 10) + "..."
        );
    }
}