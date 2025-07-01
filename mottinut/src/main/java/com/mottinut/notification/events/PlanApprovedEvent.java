package com.mottinut.notification.events;

import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class PlanApprovedEvent {
    UserId patientId;
    Long planId;
    String patientName;
    Instant occurredAt;

    public static PlanApprovedEvent of(UserId patientId, Long planId, String patientName) {
        return PlanApprovedEvent.builder()
                .patientId(patientId)
                .planId(planId)
                .patientName(patientName)
                .occurredAt(Instant.now())
                .build();
    }
}