package com.mottinut.notification.presentation.response;

import com.mottinut.notification.domain.entities.NotificationLog;
import com.mottinut.notification.domain.valueobjects.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHistoryResponse {
    private String title;
    private String body;
    private NotificationStatus status;
    private LocalDateTime createdAt;

    public static NotificationHistoryResponse from(NotificationLog log) {
        return NotificationHistoryResponse.builder()
                .title(log.getContent().getTitle())
                .body(log.getContent().getBody())
                .status(log.getStatus())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
