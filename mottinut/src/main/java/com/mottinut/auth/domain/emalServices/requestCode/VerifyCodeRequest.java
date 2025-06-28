package com.mottinut.auth.domain.emalServices.requestCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {

    @NotBlank(message = "El código es requerido")
    @Pattern(regexp = "\\d{6}", message = "El código debe tener exactamente 6 dígitos")
    private String code;

    @NotBlank(message = "El tipo de verificación es requerido")
    @Pattern(regexp = "(?i)email|sms|whatsapp", message = "Tipo de verificación inválido")
    private String type;

    private String email;


}
