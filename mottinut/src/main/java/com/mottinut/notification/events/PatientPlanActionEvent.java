package com.mottinut.notification.events;

import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class PatientPlanActionEvent {
    UserId patientId;
    UserId nutritionistId;
    Long planId;
    String patientName;
    String actionType; // "ACCEPTED", "REJECTED", "MODIFIED"
    String reason;
    Instant occurredAt;

    public static PatientPlanActionEvent of(UserId patientId, UserId nutritionistId, Long planId,
                                            String patientName, String actionType, String reason) {
        return PatientPlanActionEvent.builder()
                .patientId(patientId)
                .nutritionistId(nutritionistId)
                .planId(planId)
                .patientName(patientName)
                .actionType(actionType)
                .reason(reason)
                .occurredAt(Instant.now())
                .build();
    }
}
