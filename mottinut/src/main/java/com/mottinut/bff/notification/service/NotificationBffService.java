package com.mottinut.bff.notification.service;

import com.mottinut.bff.notification.dto.request.RegisterFcmTokenRequestDto;
import com.mottinut.bff.notification.dto.response.NotificationHistoryResponseDto;
import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.notification.domain.entities.Notification;
import com.mottinut.notification.domain.services.NotificationService;
import com.mottinut.notification.domain.services.UserTokenService;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationBffService {

    private final UserTokenService userTokenService;
    private final NotificationService notificationService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NotificationBffService(UserTokenService userTokenService, NotificationService notificationService) {
        this.userTokenService = userTokenService;
        this.notificationService = notificationService;
    }

    public void registerFcmToken(RegisterFcmTokenRequestDto request, Authentication authentication) {
        UserId userId = getCurrentUserId(authentication);
        userTokenService.saveFcmToken(userId, request.getFcmToken());
    }

    public void removeFcmToken(Authentication authentication) {
        UserId userId = getCurrentUserId(authentication);
        userTokenService.removeFcmToken(userId);
    }

    public List<NotificationHistoryResponseDto> getNotificationHistory(Authentication authentication) {
        UserId userId = getCurrentUserId(authentication);
        List<Notification> notifications = notificationService.getNotificationHistory(userId);

        return notifications.stream()
                .map(this::buildNotificationHistoryResponse)
                .collect(Collectors.toList());
    }

    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }

    private NotificationHistoryResponseDto buildNotificationHistoryResponse(Notification notification) {
        return NotificationHistoryResponseDto.builder()
                .notificationId(notification.getNotificationId().getValue())
                .type(notification.getType().getDisplayName())
                .title(notification.getTitle())
                .body(notification.getBody())
                .status(notification.getStatus().name())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt().format(DATE_FORMATTER))
                .sentAt(notification.getSentAt() != null ?
                        notification.getSentAt().format(DATE_FORMATTER) : null)
                .build();
    }
}
