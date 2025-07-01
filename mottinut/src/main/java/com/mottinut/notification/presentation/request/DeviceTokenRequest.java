package com.mottinut.notification.presentation.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceTokenRequest {
    @NotBlank(message = "Device token is required")
    @Size(max = 500, message = "Device token is too long")
    private String deviceToken;

    @NotBlank(message = "Platform is required")
    @Pattern(regexp = "^(android|ios|web)$", message = "Platform must be android, ios, or web")
    private String platform;
}
