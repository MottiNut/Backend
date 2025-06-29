package com.mottinut.auth.domain.emalServices.requestCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResendCodeRequest {
    @NotBlank(message = "El tipo de verificación es requerido")
    @Pattern(regexp = "email|sms|whatsapp", message = "Tipo de verificación inválido")
    private String type;
}