package com.mottinut.notification.domain.valueobjects;

import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.Builder;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Value
@Builder
public class NotificationContent {
    String title;
    String body;
    Map<String, String> data;

    public static NotificationContent planApproved(Long planId, UserId patientId) {
        return NotificationContent.builder()
                .title("¡Plan Nutricional Aprobado!")
                .body("Tu plan nutricional ha sido aprobado por el nutricionista")
                .data(Map.of(
                        "type", "PLAN_APPROVED",
                        "planId", planId.toString(),
                        "patientId", patientId.getValue().toString()
                ))
                .build();
    }

    public static NotificationContent planRejected(Long planId, UserId patientId, String reason) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "PLAN_REJECTED");
        data.put("planId", planId.toString());
        data.put("patientId", patientId.getValue().toString());
        data.put("reason", Optional.ofNullable(reason).orElse(""));

        return NotificationContent.builder()
                .title("Plan Nutricional Requiere Modificaciones")
                .body("Tu nutricionista ha solicitado algunos ajustes en tu plan")
                .data(data)
                .build();
    }
}
