package com.mottinut.bff.notification.controller;

import com.mottinut.bff.notification.dto.request.RegisterFcmTokenRequestDto;
import com.mottinut.bff.notification.dto.response.NotificationHistoryResponseDto;
import com.mottinut.bff.notification.service.NotificationBffService;
import com.mottinut.shared.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationBffService notificationBffService;

    public NotificationController(NotificationBffService notificationBffService) {
        this.notificationBffService = notificationBffService;
    }

    @PostMapping("/register-token")
    public ResponseEntity<com.mottinut.shared.presentation.dto.ApiResponse<Void>> registerFcmToken(
            @Valid @RequestBody RegisterFcmTokenRequestDto request,
            Authentication authentication) {
        notificationBffService.registerFcmToken(request, authentication);
        return ResponseEntity.ok(new ApiResponse<>("FCM token registrado exitosamente", null));
    }

    @DeleteMapping("/remove-token")
    public ResponseEntity<ApiResponse<Void>> removeFcmToken(Authentication authentication) {
        notificationBffService.removeFcmToken(authentication);
        return ResponseEntity.ok(new ApiResponse<>("FCM token removido exitosamente", null));
    }

    @GetMapping("/history")
    public ResponseEntity<List<NotificationHistoryResponseDto>> getNotificationHistory(Authentication authentication) {
        List<NotificationHistoryResponseDto> history = notificationBffService.getNotificationHistory(authentication);
        return ResponseEntity.ok(history);
    }
}

