package com.mottinut.bff.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String role;
    private String fullName;

    // Nuevos campos para verificación
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean fullyVerified;
    private boolean requiresVerification;
    private String message;
}
