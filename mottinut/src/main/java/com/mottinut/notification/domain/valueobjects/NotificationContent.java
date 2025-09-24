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

    public static NotificationContent planAcceptedByPatient(Long planId, String patientName, UserId nutritionistId) {
        return NotificationContent.builder()
                .title("✅ Plan Aceptado por Paciente")
                .body(patientName + " ha aceptado el plan nutricional")
                .data(Map.of(
                        "type", "PLAN_ACCEPTED_BY_PATIENT",
                        "planId", planId.toString(),
                        "patientName", patientName,
                        "nutritionistId", nutritionistId.getValue().toString()
                ))
                .build();
    }

    public static NotificationContent planRejectedByPatient(Long planId, String patientName, UserId nutritionistId, String reason) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "PLAN_REJECTED_BY_PATIENT");
        data.put("planId", planId.toString());
        data.put("patientName", patientName);
        data.put("nutritionistId", nutritionistId.getValue().toString());
        data.put("reason", Optional.ofNullable(reason).orElse("Sin motivo especificado"));

        return NotificationContent.builder()
                .title("❌ Plan Rechazado por Paciente")
                .body(patientName + " ha rechazado el plan nutricional")
                .data(data)
                .build();
    }

    public static NotificationContent planModifiedByPatient(Long planId, String patientName, UserId nutritionistId) {
        return NotificationContent.builder()
                .title("✏️ Plan Modificado por Paciente")
                .body(patientName + " ha solicitado modificaciones al plan")
                .data(Map.of(
                        "type", "PLAN_MODIFIED_BY_PATIENT",
                        "planId", planId.toString(),
                        "patientName", patientName,
                        "nutritionistId", nutritionistId.getValue().toString()
                ))
                .build();
    }

    public static NotificationContent newPatientAssigned(String patientName, UserId nutritionistId) {
        return NotificationContent.builder()
                .title("👋 Nuevo Paciente Asignado")
                .body("Tienes un nuevo paciente: " + patientName)
                .data(Map.of(
                        "type", "NEW_PATIENT_ASSIGNED",
                        "patientName", patientName,
                        "nutritionistId", nutritionistId.getValue().toString()
                ))
                .build();
    }
}
