package com.mottinut.notification.presentation;


import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.notification.domain.services.DeviceTokenService;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/notifications")
@Validated
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    public DeviceTokenController(DeviceTokenService deviceTokenService) {
        this.deviceTokenService = deviceTokenService;
    }

    @PostMapping("/device-token")
    public ResponseEntity<Void> saveDeviceToken(
            @Valid @RequestBody DeviceTokenRequest request,
            Authentication authentication) {

        UserId userId = getCurrentUserId(authentication);
        deviceTokenService.saveDeviceToken(userId, request.getDeviceToken());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/device-token")
    public ResponseEntity<Void> removeDeviceToken(Authentication authentication) {
        UserId userId = getCurrentUserId(authentication);
        deviceTokenService.removeDeviceToken(userId);

        return ResponseEntity.ok().build();
    }

    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }


}