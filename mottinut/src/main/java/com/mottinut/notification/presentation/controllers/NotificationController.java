package com.mottinut.notification.presentation.controllers;

import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.notification.domain.entities.NotificationLog;
import com.mottinut.notification.domain.repository.NotificationRepository;
import com.mottinut.notification.domain.services.NotificationDomainService;
import com.mottinut.notification.domain.valueobjects.DeviceToken;
import com.mottinut.notification.domain.valueobjects.Platform;
import com.mottinut.notification.presentation.request.DeviceTokenRequest;
import com.mottinut.notification.presentation.response.NotificationHistoryResponse;
import com.mottinut.shared.domain.valueobjects.UserId;
import com.mottinut.shared.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
@Slf4j
public class NotificationController {
    private final NotificationDomainService notificationDomainService;
    private final NotificationRepository notificationRepository;

    @PostMapping("/device-token")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<Void>> registerDeviceToken(
            @Valid @RequestBody DeviceTokenRequest request,
            Authentication authentication) {

        try {
            UserId userId = getCurrentUserId(authentication);
            Platform platform = Platform.valueOf(request.getPlatform().toUpperCase());
            DeviceToken deviceToken = DeviceToken.of(request.getDeviceToken(), platform);

            notificationDomainService.registerDeviceToken(userId, deviceToken);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Device token registered successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid request: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error registering device token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }

    @DeleteMapping("/device-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> removeDeviceToken(Authentication authentication) {
        try {
            UserId userId = getCurrentUserId(authentication);
            notificationDomainService.removeDeviceToken(userId);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Error removing device token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<NotificationHistoryResponse>>> getNotificationHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {

        try {
            UserId userId = getCurrentUserId(authentication);
            List<NotificationLog> logs = notificationRepository.findRecentByUserId(userId, limit);

            List<NotificationHistoryResponse> response = logs.stream()
                    .map(NotificationHistoryResponse::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            log.error("Error fetching notification history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }

    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }
}

