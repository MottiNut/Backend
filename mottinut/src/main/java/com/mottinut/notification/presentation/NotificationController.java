package com.mottinut.notification.presentation;

import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.notification.domain.services.NotificationService;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/register-token")
    public ResponseEntity<Void> registerDeviceToken(@RequestBody Map<String, String> request,
                                                    Authentication authentication) {
        UserId userId = getCurrentUserId(authentication);
        String deviceToken = request.get("deviceToken");

        notificationService.saveDeviceToken(userId, deviceToken);
        return ResponseEntity.ok().build();
    }

    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }
}

