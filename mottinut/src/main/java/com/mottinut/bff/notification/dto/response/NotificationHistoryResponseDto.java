package com.mottinut.bff.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHistoryResponseDto {
    private String notificationId;
    private String type;
    private String title;
    private String body;
    private String status;
    private Long referenceId;
    private String createdAt;
    private String sentAt;
}
