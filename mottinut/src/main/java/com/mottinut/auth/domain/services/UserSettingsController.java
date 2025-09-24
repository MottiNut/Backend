package com.mottinut.auth.domain.services;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.auth.domain.services.UserSettingsService;
import com.mottinut.shared.domain.valueobjects.UserId;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserService userService;
    private final UserSettingsService settingsService;

    /**
     * Consultar estado de onboarding
     */
    @GetMapping("/onboarding")
    public ResponseEntity<Map<String, Boolean>> getOnboardingStatus(@PathVariable Long userId) {
        User user = userService.getUserById(new UserId(userId));
        var settings = settingsService.getOrCreateSettings(user);
        return ResponseEntity.ok(Map.of("onboardingSeen", settings.isOnboardingSeen()));
    }

    /**
     * Marcar onboarding como visto
     */
    @PostMapping("/onboarding")
    public ResponseEntity<Void> markOnboardingSeen(@PathVariable String userId) {
        User user = getUser(userId);
        settingsService.setOnboardingSeen(user, true);
        return ResponseEntity.ok().build();
    }

    /**
     * Consultar estado de toolTips
     */
    @GetMapping("/tooltips")
    public ResponseEntity<Map<String, Boolean>> getToolTipsStatus(@PathVariable String userId) {
        User user = getUser(userId);
        var settings = settingsService.getOrCreateSettings(user);
        return ResponseEntity.ok(Map.of("toolTipsSeen", settings.isToolTipsSeen()));
    }

    /**
     * Marcar toolTips como visto
     */
    @PostMapping("/tooltips")
    public ResponseEntity<Void> markToolTipsSeen(@PathVariable String userId) {
        User user = getUser(userId);
        settingsService.setToolTipsSeen(user, true);
        return ResponseEntity.ok().build();
    }

    private User getUser(String userIdStr) {
        Long userIdLong;
        try {
            userIdLong = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID de usuario inválido: " + userIdStr);
        }

        UserId userId = new UserId(userIdLong);
        return userService.getUserById(userId);
    }

}


