package com.mottinut.notification.presentation.controllers;

import com.mottinut.notification.domain.services.NutritionistNotificationService;
import com.mottinut.notification.events.PatientPlanActionEvent;
import com.mottinut.notification.presentation.request.PatientPlanActionRequest;
import com.mottinut.shared.domain.valueobjects.UserId;
import com.mottinut.shared.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/nutritionist")
@RequiredArgsConstructor
@Slf4j
public class NutritionistNotificationController {

    private final NutritionistNotificationService nutritionistNotificationService;

    @PostMapping("/patient-action")
    public ResponseEntity<ApiResponse<Void>> handlePatientAction(
            @Valid @RequestBody PatientPlanActionRequest request) {
        log.info("📩 [INICIO] Notificación de acción de paciente: {}", request);
        try {
            UserId patientId = UserId.of(request.getPatientId());
            UserId nutritionistId = UserId.of(request.getNutritionistId());
            log.debug("✅ Datos recibidos -> patientId: {}, nutritionistId: {}, planId: {}",
                    patientId, nutritionistId, request.getPlanId());

            PatientPlanActionEvent event = PatientPlanActionEvent.of(
                    patientId, nutritionistId, request.getPlanId(),
                    request.getPatientName(), request.getActionType(), request.getReason()
            );

            nutritionistNotificationService.handlePatientPlanAction(event);
            log.info("🎯 Notificación enviada correctamente a nutricionista: {}", nutritionistId);

            return ResponseEntity.ok(ApiResponse.success("Notificación enviada al nutricionista"));

        } catch (Exception e) {
            log.error("❌ Error procesando acción del paciente: ", e); // <---- stacktrace completo
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error procesando la acción"));
        }
    }

    @PostMapping("/new-patient")
    public ResponseEntity<ApiResponse<Void>> notifyNewPatient(
            @RequestParam Long nutritionistId,
            @RequestParam String patientName) {

        try {
            UserId nutriId = UserId.of(nutritionistId);
            nutritionistNotificationService.notifyNewPatientAssignment(nutriId, patientName);

            return ResponseEntity.ok(ApiResponse.success("Notificación de nuevo paciente enviada"));

        } catch (Exception e) {
            log.error("Error notificando nuevo paciente: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error enviando notificación"));
        }
    }
}