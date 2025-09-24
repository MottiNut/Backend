package com.mottinut.notification.domain.services;

import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.notification.events.PatientPlanActionEvent;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NutritionistNotificationService {

    private final NotificationDomainService notificationDomainService;

    public void handlePatientPlanAction(PatientPlanActionEvent event) {
        try {
            UserId nutritionistId = event.getNutritionistId();

            switch (event.getActionType()) {
                case "ACCEPTED":
                    NotificationContent acceptedContent = NotificationContent.planAcceptedByPatient(
                            event.getPlanId(), event.getPatientName(), nutritionistId
                    );
                    notificationDomainService.sendNotification(nutritionistId, acceptedContent);
                    break;

                case "REJECTED":
                    NotificationContent rejectedContent = NotificationContent.planRejectedByPatient(
                            event.getPlanId(), event.getPatientName(), nutritionistId, event.getReason()
                    );
                    notificationDomainService.sendNotification(nutritionistId, rejectedContent);
                    break;

                case "MODIFIED":
                    NotificationContent modifiedContent = NotificationContent.planModifiedByPatient(
                            event.getPlanId(), event.getPatientName(), nutritionistId
                    );
                    notificationDomainService.sendNotification(nutritionistId, modifiedContent);
                    break;
            }

            log.info("Notificación enviada al nutricionista {} por acción {} del paciente {}",
                    nutritionistId.getValue(), event.getActionType(), event.getPatientName());

        } catch (Exception e) {
            log.error("Error enviando notificación al nutricionista: {}", e.getMessage(), e);
        }
    }

    public void notifyNewPatientAssignment(UserId nutritionistId, String patientName) {
        try {
            NotificationContent content = NotificationContent.newPatientAssigned(
                    patientName, nutritionistId
            );
            notificationDomainService.sendNotification(nutritionistId, content);

            log.info("Notificación de nuevo paciente enviada al nutricionista {}: {}",
                    nutritionistId.getValue(), patientName);

        } catch (Exception e) {
            log.error("Error enviando notificación de nuevo paciente: {}", e.getMessage(), e);
        }
    }
}
