package com.mottinut.auth.domain.emalServices.responsiveStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationStatusResponse {
    private boolean success;
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean fullyVerified;
    private String emailVerifiedAt;
    private String phoneVerifiedAt;
    private String message;


}